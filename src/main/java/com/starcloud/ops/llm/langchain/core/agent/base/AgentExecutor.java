package com.starcloud.ops.llm.langchain.core.agent.base;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.llm.langchain.core.agent.base.action.AgentAction;
import com.starcloud.ops.llm.langchain.core.agent.base.action.AgentFinish;
import com.starcloud.ops.llm.langchain.core.agent.base.action.FunctionsAgentAction;
import com.starcloud.ops.llm.langchain.core.callbacks.CallbackManager;
import com.starcloud.ops.llm.langchain.core.chain.base.Chain;
import com.starcloud.ops.llm.langchain.core.memory.BaseChatMemory;
import com.starcloud.ops.llm.langchain.core.model.chat.base.BaseChatModel;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.model.llm.base.ChatGeneration;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.BaseLanguageModel;
import com.starcloud.ops.llm.langchain.core.callbacks.BaseCallbackManager;
import com.starcloud.ops.llm.langchain.core.schema.message.AIMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.FunctionMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.HumanMessage;
import com.starcloud.ops.llm.langchain.core.schema.parser.OutputParserException;
import com.starcloud.ops.llm.langchain.core.tools.FailTool;
import com.starcloud.ops.llm.langchain.core.tools.InvalidTool;
import com.starcloud.ops.llm.langchain.core.tools.base.BaseTool;
import com.starcloud.ops.llm.langchain.core.tools.base.FunTool;
import com.starcloud.ops.llm.langchain.core.tools.base.ToolResponse;
import com.starcloud.ops.llm.langchain.core.tools.exception.FailToolExecution;
import com.starcloud.ops.llm.langchain.core.tools.exception.InvalidToolExecution;
import com.starcloud.ops.llm.langchain.core.tools.exception.ToolContinuesExecution;
import com.theokanning.openai.completion.chat.ChatFunctionCall;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Data
public class AgentExecutor extends Chain<AgentFinish> {

    private BaseChatMemory memory;

    private BaseSingleActionAgent actionAgent;

    private List<BaseTool> tools;

    private BaseCallbackManager callbackManager;

    private List<String> tags;

    private Boolean returnIntermediateSteps = true;

    //最大工具执行次数
    private int maxIterations = 6;

    //最大工具执行总耗时
    private float maxExecutionTime = 1000 * 60 * 5;

    private String earlyStoppingMethod = "force";

    public static String AgentFinishInputKey = "agentFinish_input";

    private List<Object> handleParsingErrors;

    /**
     * 输入的字段
     */
    private List<String> inputKeys = Arrays.asList("input");

    private AgentExecutor(BaseSingleActionAgent actionAgent, List<BaseTool> tools, BaseCallbackManager callbackManager, List<String> tags) {
        this.actionAgent = actionAgent;
        this.tools = tools;
        this.callbackManager = callbackManager;
        this.tags = tags;
        Optional.ofNullable(this.tools).orElse(new ArrayList<>()).forEach(tool -> {
            tool.setCallbackManager(callbackManager);
        });
        actionAgent.setAgentExecutor(this);
    }

    public static AgentExecutor fromAgentAndTools(BaseSingleActionAgent agent, List<BaseTool> tools, BaseCallbackManager callbackManager) {

        Assert.notNull(agent);
        return new AgentExecutor(agent, tools, callbackManager, new ArrayList<>());
    }

    public static AgentExecutor fromAgentAndTools(BaseSingleActionAgent agent, List<BaseTool> tools, BaseCallbackManager callbackManager, BaseChatMemory chatMemory) {

        Assert.notNull(agent);
        AgentExecutor agentExecutor = new AgentExecutor(agent, tools, callbackManager, new ArrayList<>());
        agentExecutor.setMemory(chatMemory);

        return agentExecutor;
    }


    protected static AgentExecutor loadAgent() {
        return null;
    }

    protected Map<String, BaseTool> getToolMaps() {

        Map<String, BaseTool> toolMap = Optional.ofNullable(this.getTools()).orElse(new ArrayList<>()).stream().collect(Collectors.toMap(BaseTool::getName, Function.identity()));
        return toolMap;
    }

    @Override
    protected AgentFinish _call(List<BaseVariable> variables) {

        List<AgentAction> intermediateSteps = new ArrayList<>();

        int iterations = 0;
        long timeElapsed = 0;
        TimeInterval timer = DateUtil.timer();

        Map<String, BaseTool> toolMap = this.getToolMaps();

        while (this._shouldContinue(iterations, timeElapsed)) {

            List<AgentAction> nextStepOutput = this._takeNextStep(toolMap, variables, intermediateSteps);
            //检查是否直接有完成的AgentFinish
            AgentFinish agentFinish = this.checkGetAgentFinish(nextStepOutput);

            //有AgentFinish 直接返回
            if (agentFinish != null) {
                return this._return(agentFinish, intermediateSteps);
            }

            //FunctionAction 增加到actions执行记录
            intermediateSteps.addAll(nextStepOutput);

            if (CollectionUtil.size(nextStepOutput) == 1) {
                FunctionsAgentAction nextStepAction = (FunctionsAgentAction) nextStepOutput.get(0);

                //@todo 无用不会返回
                AgentFinish toolReturn = getToolReturn(nextStepAction);
                if (toolReturn != null) {
                    return this._return(toolReturn, intermediateSteps);
                }
            }
            iterations += 1;
            timeElapsed = timer.interval();
        }

        //执行到这说明已经超时了，返回一个超时的 AgentFinish
        AgentFinish stopAgent = this.actionAgent.returnStoppedResponse("force", intermediateSteps, iterations, timeElapsed);

        //超时这里要增加日志
        BaseLLMResult baseLLMResult = this.parseAgentAction2LLmResult(stopAgent);
        if (this.getMemory() != null) {
            this.getMemory().saveContext(null, baseLLMResult);
        }

        return this._return(stopAgent, intermediateSteps);
    }

    @Override
    public String run(List<BaseVariable> baseVariables) {

        AgentAction response = this.call(baseVariables);
        if (response != null) {
            return response.getObservation();
        }
        return "";
    }

    @Override
    protected AgentFinish prepOutputs(List<BaseVariable> baseVariables, AgentFinish result) {

        this._validateOutputs(result);

        if (this.getMemory() != null) {
            //只会是 AgentFinish
            if (result instanceof AgentFinish) {
                //已经在 上游流程中saveContext了，这里不在处理了
//                BaseLLMResult baseLLMResult = this.parseAgentAction2LLmResult(result);
//                this.getMemory().saveContext(null, baseLLMResult);
            }
        }

        return result;
    }


    protected BaseLLMResult parseAgentAction2LLmResult(List<BaseVariable> variables, List<AgentAction> intermediateSteps, AgentAction actionAgent) {

        List<ChatGeneration<Object>> generations = new ArrayList<>();


        /**
         * 一个 FunctionsAgentAction 有两种状态，执行前，执行后
         */
        if (actionAgent instanceof FunctionsAgentAction) {

            AIMessage aiMessage = (AIMessage) ((FunctionsAgentAction) actionAgent).getMessagesLog().stream().findFirst().get();

            if (aiMessage.getAdditionalArgs() != null && aiMessage.getAdditionalArgs().get("function_call") != null) {
                //有状态，执行过
                if (actionAgent.getStatus() != null) {

                    ChatFunctionCall functionCall = (ChatFunctionCall) aiMessage.getAdditionalArgs().get("function_call");
                    JsonNode params = functionCall.getArguments();

                    FunctionMessage functionMessage = new FunctionMessage(((FunctionsAgentAction) actionAgent).getTool(), params);
                    functionMessage.setContent(JSONUtil.toJsonStr(actionAgent.getObservation()));
                    functionMessage.setStatus(actionAgent.getStatus());
                    functionMessage.setElapsed(((FunctionsAgentAction) actionAgent).getElapsed());

                    //@todo 如果要增加工具消耗，还没有地方加
                    generations.add(ChatGeneration.builder().generationInfo(actionAgent).chatMessage(functionMessage).build());

                } else {

                    //未执行过，即 请求LLM返回fun_call
                    generations.add(ChatGeneration.builder().generationInfo(actionAgent).chatMessage(aiMessage).build());
                }
            }

        } else if (actionAgent instanceof AgentFinish) {


            if (Boolean.TRUE.equals(actionAgent.getStatus())) {

                //LLM 用函数结果调用后返回最终结果
                List<BaseMessage> messageList = ((AgentFinish) actionAgent).getMessagesLog();

                AIMessage aiMessage = (AIMessage) messageList.get(0);
                aiMessage.getAdditionalArgs().put(AgentFinishInputKey, actionAgent.getLog());

                generations.add(ChatGeneration.builder().generationInfo(actionAgent).chatMessage(aiMessage).build());

            } else {

                generations.add(ChatGeneration.builder().generationInfo(actionAgent).chatMessage(null).build());
                //agent执行异常，提前结束了，如循环超时
                log.error("agentExecutor parseAgentAction2LLmResult is fail, AgentFinish status Illegal: {}", actionAgent);

            }


        } else {

            log.error("agentExecutor parseAgentAction2LLmResult is fail, Unsupported actionAgent: {}", actionAgent);
        }

        //第一次请求
        if (CollectionUtil.isEmpty(intermediateSteps)) {

            BaseVariable variable = BaseVariable.findVariable(variables, this.getInputKeys().get(0));
            HumanMessage humanMessage = new HumanMessage(String.valueOf(variable.getValue()));

            //用户请求放在第一个
            generations.add(0, ChatGeneration.builder().chatMessage(humanMessage).build());
        }

        BaseLLMResult baseLLMResult = BaseLLMResult.builder().generations(generations).build();

        return baseLLMResult;
    }


    protected BaseLLMResult parseAgentAction2LLmResult(AgentAction actionAgent) {

        List<ChatGeneration<Object>> generations = new ArrayList<>();

        /**
         * 一个 FunctionsAgentAction 有两种状态，执行前，执行后
         */
        if (actionAgent instanceof FunctionsAgentAction) {

            AIMessage aiMessage = (AIMessage) ((FunctionsAgentAction) actionAgent).getMessagesLog().stream().findFirst().get();

            if (aiMessage.getAdditionalArgs() != null && aiMessage.getAdditionalArgs().get("function_call") != null) {
                //有状态，执行过
                if (actionAgent.getStatus() != null) {

                    ChatFunctionCall functionCall = (ChatFunctionCall) aiMessage.getAdditionalArgs().get("function_call");
                    JsonNode params = functionCall.getArguments();

                    FunctionMessage functionMessage = new FunctionMessage(((FunctionsAgentAction) actionAgent).getTool(), params);
                    functionMessage.setContent(actionAgent.getObservation());
                    functionMessage.setStatus(actionAgent.getStatus());
                    functionMessage.setElapsed(((FunctionsAgentAction) actionAgent).getElapsed());

                    //@todo 如果要增加工具消耗，还没有地方加
                    generations.add(ChatGeneration.builder().generationInfo(actionAgent).chatMessage(functionMessage).build());

                } else {

                    //未执行过，即 请求LLM返回fun_call
                    generations.add(ChatGeneration.builder().generationInfo(actionAgent).chatMessage(aiMessage).build());
                }
            }

        } else if (actionAgent instanceof AgentFinish) {


            if (Boolean.TRUE.equals(actionAgent.getStatus())) {

                //LLM 用函数结果调用后返回最终结果
                List<BaseMessage> messageList = ((AgentFinish) actionAgent).getMessagesLog();

                AIMessage aiMessage = (AIMessage) messageList.get(0);
                aiMessage.getAdditionalArgs().put(AgentFinishInputKey, actionAgent.getLog());

                generations.add(ChatGeneration.builder().generationInfo(actionAgent).chatMessage(aiMessage).build());

            } else {

                generations.add(ChatGeneration.builder().generationInfo(actionAgent).chatMessage(null).build());
                //agent执行异常，提前结束了，如循环超时
                log.error("agentExecutor parseAgentAction2LLmResult is fail, AgentFinish status Illegal: {}", actionAgent);

            }


        } else {

            log.error("agentExecutor parseAgentAction2LLmResult is fail, Unsupported actionAgent: {}", actionAgent);
        }

        BaseLLMResult baseLLMResult = BaseLLMResult.builder().generations(generations).build();

        return baseLLMResult;
    }


    @Override
    public String run(String text) {

        return this.run(Arrays.asList(BaseVariable.newString(this.getInputKeys().get(0), text)));
    }


    /**
     * 判断执行条件是否满足
     *
     * @param iterations
     * @param timeElapsed
     * @return
     * @todo 返回异常，让上游感知到
     */
    protected Boolean _shouldContinue(Integer iterations, long timeElapsed) {
        if (iterations > this.getMaxIterations()) {
            log.info("_shouldContinue is skip, more MaxIterations");
            return false;
        }

        if (timeElapsed > this.getMaxExecutionTime()) {

            log.info("_shouldContinue is skip, more tMaxExecutionTime");
            return false;
        }

        return true;
    }

    @Override
    public void save() {
        return;
    }

    public void saveAgent() {
        return;
    }


    public BaseTool lookupTool(String name) {

        return null;
    }


    /**
     * 执行LLM，包装成不同类型的AgentAction
     *
     * @param toolMap
     * @param variables
     * @param intermediateSteps
     * @return
     */
    private List<AgentAction> _takeNextStep(Map<String, BaseTool> toolMap, List<BaseVariable> variables, List<AgentAction> intermediateSteps) {

        //llm执行的任何异常都应阻断流程，所以此处无catch

        List<AgentAction> nextStepOutput = this.getActionAgent().plan(intermediateSteps, variables, this.getCallbackManager());

        if (CollectionUtil.size(nextStepOutput) != 1) {
            throw new OutputParserException("plan return is error, agetAction more 1");
        }

        //增加 fun 调用日志
        Optional.ofNullable(nextStepOutput).orElse(new ArrayList<>()).forEach(agentAction -> {

            BaseLLMResult baseLLMResult = this.parseAgentAction2LLmResult(variables, intermediateSteps, agentAction);

            if (this.getMemory() != null) {
                this.getMemory().saveContext(null, baseLLMResult);
            }

        });

        //判断是否完成了，现在只会有一个元素
        if (CollectionUtil.size(nextStepOutput) == 1) {
            if (nextStepOutput.get(0) instanceof AgentFinish) {
                return nextStepOutput;
            }
        }

        List<AgentAction> result = new ArrayList<>();

        //现在只有会一个元素
        for (AgentAction agentAction : nextStepOutput) {
            FunctionsAgentAction funAgentAction = (FunctionsAgentAction) agentAction;

            long start = System.currentTimeMillis();
            this.callbackManager.onAgentAction(this.getClass(), agentAction, this.getVerbose());

            String tool = funAgentAction.getTool();
            Object toolInput = funAgentAction.getToolInput();
            ToolResponse toolResponse = null;

            /**
             * 执行函数调用，异常catch，保存下来.并且设置返回内容，好让LLM继续做判断
             */
            //现在只会有 FunTool，都是 FunTool 包装后的tool
            BaseTool baseTool = toolMap.get(tool);

            try {
                if (toolMap.containsKey(tool)) {
                    Map<String, Object> toolRunKwargs = this.actionAgent.toolRunLoggingKwargs();

                    if (baseTool.getReturnDirect()) {
                        toolRunKwargs.put("llm_prefix", "");
                    }

                    //@todo 捕获工具执行异常
                    toolResponse = baseTool.run(toolInput, this.getVerbose(), toolRunKwargs);
                    //为空，可能执行异常，告诉LLM 此次调用失败，无效
                    if (ObjectUtil.isEmpty(toolResponse.getObservation())) {
                        throw new FailToolExecution(tool, toolInput, -1, "");
                    }

                    funAgentAction.setStatus(true);
                    funAgentAction.setObservation(toolResponse.getObservation());
                    funAgentAction.setToolResponse(toolResponse.getResponse());

                } else {

                    throw new InvalidToolExecution(tool, toolInput);
                }

            } catch (ToolContinuesExecution toolContinuesExecution) {

                log.error("AgentExecutor tool [{}] run is error: {}", toolContinuesExecution.getToolName(), toolContinuesExecution);

                funAgentAction.setStatus(false);
                funAgentAction.setErrorCode(toolContinuesExecution.getErrorCode());
                funAgentAction.setError(toolContinuesExecution.getMessage());

                funAgentAction.setObservation(toolContinuesExecution.getObservation());

            } catch (Exception e) {

                //所有到工具执行异常，都返回 FailTool 即可，交由LLM继续执行后续逻辑

                log.error("AgentExecutor tool  [{}]  run is fail: {}", baseTool.getClass().getSimpleName(), e.getMessage(), e);

                funAgentAction.setStatus(false);
                funAgentAction.setErrorCode(-2);
                funAgentAction.setError(e.getMessage());

                funAgentAction.setObservation(new FailToolExecution(tool, toolInput, -1, "").getObservation());

            } finally {

                funAgentAction.setElapsed(System.currentTimeMillis() - start);
                //增加 fun 调用日志
                BaseLLMResult baseLLMResult = this.parseAgentAction2LLmResult(funAgentAction);
                if (this.getMemory() != null) {
                    this.getMemory().saveContext(null, baseLLMResult);
                }
            }

            result.add(funAgentAction);
        }

        return result;
    }


    private AgentFinish checkGetAgentFinish(List<AgentAction> agentActions) {

        if (CollectionUtil.size(agentActions) == 1) {
            if (agentActions.get(0) instanceof AgentFinish) {
                return (AgentFinish) agentActions.get(0);
            }
        }

        return null;
    }

    private AgentFinish getToolReturn(FunctionsAgentAction nextStepAction) {

        Map<String, BaseTool> toolMap = this.getToolMaps();

        if (toolMap.containsKey(nextStepAction.getTool())) {

            if (toolMap.get(nextStepAction.getTool()).getReturnDirect()) {

                return new AgentFinish(nextStepAction.getObservation(), nextStepAction.getLog());
            }
        }

        return null;
    }

    /**
     * 对返回对AgentFinish增加 执行历史的 AgentAction 的记录，其中也包括了LLM执行消耗
     *
     * @param agentFinish
     * @param agentActions
     * @return
     */
    private AgentFinish _return(AgentFinish agentFinish, List<AgentAction> agentActions) {

        this.callbackManager.onAgentFinish(this.getClass(), agentFinish);

        Map<String, Object> returnValue = agentFinish.getReturnValues();

        AgentExecutorResponse agentExecutorResponse = new AgentExecutorResponse();
        agentExecutorResponse.setOutput(agentFinish.getOutput());

        if (this.returnIntermediateSteps) {
            returnValue.put("intermediate_steps", agentActions);
            //set agentActions

            agentExecutorResponse.setIntermediateSteps(agentActions);
        }

        return agentFinish;
    }
}
