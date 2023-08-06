package com.starcloud.ops.llm.langchain.core.agent.base;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.starcloud.ops.llm.langchain.core.agent.base.action.AgentAction;
import com.starcloud.ops.llm.langchain.core.agent.base.action.AgentFinish;
import com.starcloud.ops.llm.langchain.core.callbacks.CallbackManager;
import com.starcloud.ops.llm.langchain.core.chain.base.Chain;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.BaseLanguageModel;
import com.starcloud.ops.llm.langchain.core.callbacks.BaseCallbackManager;
import com.starcloud.ops.llm.langchain.core.callbacks.CallbackManagerForChainRun;
import com.starcloud.ops.llm.langchain.core.schema.parser.OutputParserException;
import com.starcloud.ops.llm.langchain.core.tools.InvalidTool;
import com.starcloud.ops.llm.langchain.core.tools.base.BaseTool;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Data
public class AgentExecutor extends Chain<Map<String, Object>> {

    private BaseSingleActionAgent actionAgent;

    private List<BaseTool> tools;

    private BaseCallbackManager callbackManager;

    private List<String> tags;

    private Boolean returnIntermediateSteps = false;

    private int maxIterations = 6;

    private float maxExecutionTime = 1000 * 60 * 5;

    private String earlyStoppingMethod = "force";

    private List<Object> handleParsingErrors;

    private AgentExecutor(BaseSingleActionAgent actionAgent, List<BaseTool> tools, BaseCallbackManager callbackManager, List<String> tags) {
        this.actionAgent = actionAgent;
        this.tools = tools;
        this.callbackManager = callbackManager;
        this.tags = tags;
    }

    public static AgentExecutor fromAgentAndTools(List<BaseTool> tools, BaseLanguageModel llm, BaseSingleActionAgent agent, BaseCallbackManager callbackManager) {

        Assert.notNull(agent);

        return new AgentExecutor(agent, tools, callbackManager, new ArrayList<>());
    }

    public static AgentExecutor fromAgentAndTools(List<BaseTool> tools, BaseLanguageModel llm, BaseSingleActionAgent agent) {

        Assert.notNull(agent);

        return new AgentExecutor(agent, tools, new CallbackManager(), new ArrayList<>());
    }

    protected static AgentExecutor loadAgent() {
        return null;
    }


    protected Map<String, BaseTool> getToolMaps() {

        Map<String, BaseTool> toolMap = Optional.ofNullable(this.getTools()).orElse(new ArrayList<>()).stream().map(tool -> {
            tool.setCallbackManager(this.getCallbackManager());
            return tool;
        }).collect(Collectors.toMap(BaseTool::getName, Function.identity()));
        return toolMap;
    }

    @Override
    protected Map<String, Object> _call(List<BaseVariable> variables) {

        List<AgentAction> intermediateSteps = new ArrayList<>();

        int iterations = 0;
        long timeElapsed = 0;
        TimeInterval timer = DateUtil.timer();

        Map<String, BaseTool> toolMap = this.getToolMaps();

        while (this._shouldContinue(iterations, timeElapsed)) {

            List<AgentAction> nextStepOutput = this._takeNextStep(toolMap, variables, intermediateSteps);

            AgentFinish agentFinish = this.checkGetAgentFinish(nextStepOutput);

            if (agentFinish != null) {
                return this._return(agentFinish, intermediateSteps);
            }

            intermediateSteps.addAll(nextStepOutput);

            if (CollectionUtil.size(nextStepOutput) == 1) {
                AgentAction nextStepAction = nextStepOutput.get(0);

                AgentFinish toolReturn = getToolReturn(nextStepAction);
                if (toolReturn != null) {
                    this._return(toolReturn, intermediateSteps);
                }
            }
            iterations += 1;
            timeElapsed = timer.interval();
        }

        AgentFinish stopAgent = this.actionAgent.returnStoppedResponse("force", intermediateSteps, variables);

        return this._return(stopAgent, intermediateSteps);
    }

    @Override
    public String run(List<BaseVariable> baseVariables) {

        Map<String, Object> result = this.call(baseVariables);
        return result.toString();
    }

    @Override
    protected Map<String, Object> prepOutputs(List<BaseVariable> baseVariables, Map<String, Object> result) {

        this._validateOutputs(result);

        if (this.getMemory() != null) {
            this.getMemory().saveContext(baseVariables, BaseLLMResult.builder().text((String) result.getOrDefault("output", "")).build());
        }
        return result;
    }


    @Override
    public String run(String text) {

        return this.run(Arrays.asList(BaseVariable.newString("input", text)));
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


    private List<AgentAction> _takeNextStep(Map<String, BaseTool> toolMap, List<BaseVariable> variables, List<AgentAction> intermediateSteps) {

        List<AgentAction> agentActions = new ArrayList<>();

        try {

            agentActions = this.getActionAgent().plan(intermediateSteps, variables, this.callbackManager);

        } catch (OutputParserException e) {

            log.error("plan is fail: {}", e.getMessage(), e);

            //ExceptionTool

            return Arrays.asList(new AgentFinish(new HashMap() {{
                put("error", e.getMessage());
            }}, ""));
        }

        //todo multistep AgentAction
        if (CollectionUtil.size(agentActions) == 1) {

            if (agentActions.get(0) instanceof AgentFinish) {
                return agentActions;
            }
        }

        List<AgentAction> result = new ArrayList<>();

        for (AgentAction agentAction : agentActions) {

            Object observation = null;

            this.callbackManager.onAgentAction(this.getClass(), agentAction, this.getVerbose());

            if (toolMap.containsKey(agentAction.getTool())) {

                BaseTool baseTool = toolMap.get(agentAction.getTool());
                Map<String, Object> toolRunKwargs = this.actionAgent.toolRunLoggingKwargs();

                if (baseTool.getReturnDirect()) {
                    toolRunKwargs.put("llm_prefix", "");
                }

                observation = baseTool.run(agentAction.getToolInput(), this.getVerbose(), toolRunKwargs);

            } else {

                Map<String, Object> toolRunKwargs = this.actionAgent.toolRunLoggingKwargs();
                InvalidTool invalidTool =  new InvalidTool();
                invalidTool.setCallbackManager(this.callbackManager);
                observation = invalidTool.run(agentAction.getToolInput(), this.getVerbose(), toolRunKwargs);
            }

            agentAction.setObservation(observation);

            result.add(agentAction);
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

    private AgentFinish getToolReturn(AgentAction nextStepAction) {

        Map<String, BaseTool> toolMap = this.getToolMaps();

        if (toolMap.containsKey(nextStepAction.getTool())) {

            if (toolMap.get(nextStepAction.getTool()).getReturnDirect()) {

                return new AgentFinish(nextStepAction.getObservation(), nextStepAction.getLog());
            }
        }

        return null;
    }

    private Map<String, Object> _return(AgentFinish agentFinish, List<AgentAction> agentActions) {

        this.callbackManager.onAgentFinish(this.getClass(), agentFinish);

        Map<String, Object> returnValue = agentFinish.getReturnValues();

        if (this.returnIntermediateSteps) {
            returnValue.put("intermediate_steps", agentActions);
            //set agentActions
        }

        return returnValue;
    }
}
