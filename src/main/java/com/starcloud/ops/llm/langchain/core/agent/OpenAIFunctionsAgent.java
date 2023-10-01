package com.starcloud.ops.llm.langchain.core.agent;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.llm.langchain.core.agent.base.action.AgentAction;
import com.starcloud.ops.llm.langchain.core.agent.base.action.AgentFinish;
import com.starcloud.ops.llm.langchain.core.agent.base.BaseSingleActionAgent;
import com.starcloud.ops.llm.langchain.core.agent.base.action.FunctionsAgentAction;
import com.starcloud.ops.llm.langchain.core.memory.BaseChatMemory;
import com.starcloud.ops.llm.langchain.core.memory.ChatMessageHistory;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;

import com.starcloud.ops.llm.langchain.core.model.chat.base.BaseChatModel;
import com.starcloud.ops.llm.langchain.core.prompt.base.HumanMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.MessagesPlaceholder;
import com.starcloud.ops.llm.langchain.core.prompt.base.PromptValue;
import com.starcloud.ops.llm.langchain.core.prompt.base.SystemMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.*;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.callbacks.BaseCallbackManager;
import com.starcloud.ops.llm.langchain.core.schema.message.AIMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.FunctionMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.SystemMessage;
import com.starcloud.ops.llm.langchain.core.schema.prompt.BasePromptTemplate;
import com.starcloud.ops.llm.langchain.core.schema.tool.FunctionDescription;
import com.starcloud.ops.llm.langchain.core.tools.base.BaseTool;
import com.theokanning.openai.completion.chat.ChatFunctionCall;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class OpenAIFunctionsAgent extends BaseSingleActionAgent {

    private BaseChatModel llm;

    private List<BaseTool> tools;

    private BasePromptTemplate promptTemplate;

    private OpenAIFunctionsAgent(BaseChatModel llm, List<BaseTool> tools, BasePromptTemplate promptTemplate) {
        super();
        this.llm = llm;
        this.tools = tools;
        this.promptTemplate = promptTemplate;
    }

    public static OpenAIFunctionsAgent fromLLMAndTools(BaseChatModel llm, List<BaseTool> tools) {

        return fromLLMAndTools(llm, tools, new ArrayList<>(), new SystemMessage("You are a helpful AI assistant."));
    }


    public static OpenAIFunctionsAgent fromLLMAndTools(BaseChatModel llm, List<BaseTool> tools, List<BaseMessagePromptTemplate> extraPromptMessages, SystemMessage systemMessage) {

        Assert.isInstanceOf(ChatOpenAI.class, llm, "Only supported with ChatOpenAI models.");

        OpenAIFunctionsAgent agent = new OpenAIFunctionsAgent(llm, tools, createPrompt(systemMessage, extraPromptMessages));

        return agent;
    }


    public static OpenAIFunctionsAgent fromLLMAndTools(BaseChatModel llm, List<BaseTool> tools, BasePromptTemplate basePromptTemplate) {

        Assert.isInstanceOf(ChatOpenAI.class, llm, "Only supported with ChatOpenAI models.");

        OpenAIFunctionsAgent agent = new OpenAIFunctionsAgent(llm, tools, basePromptTemplate);

        return agent;
    }

    @Override
    public List<AgentAction> plan(List<AgentAction> intermediateSteps, List<BaseVariable> variables, BaseCallbackManager callbackManager) {

        /**
         * 历史如何构建，是否需要特殊处理，减少整体prompt，把之前的对话内容去掉？
         * 1，保证完整性，之前内容不去掉（优先）
         * 2，精简prompt，去掉前面的内容
         *
         * 读取历史时
         * history
         *  保存时
         * AgentAction => BaseLLMResult => saveContext => db => history
         */
        List<BaseMessage> chatMessages = this.formatIntermediateSteps(intermediateSteps);

        List<BaseVariable> selectedInputs = Optional.ofNullable(variables).orElse(new ArrayList<>()).stream().filter(baseVariable -> !baseVariable.getField().equals(TEMP_VARIABLE_SCRATCHPAD)).collect(Collectors.toList());

        String historyStr = BaseMessage.getBufferString(chatMessages);

        //字符串 agent调用历史
        selectedInputs.add(BaseVariable.newObject(TEMP_VARIABLE_SCRATCHPAD, historyStr));

        PromptValue promptValue = this.promptTemplate.formatPrompt(selectedInputs);
        List<BaseMessage> messages = promptValue.toMessage();

        BaseMessage predictedMessage = this.llm.predictMessages(messages, null, this.getFunctions(), callbackManager);

        AgentAction agentAction = parseAiMessage(predictedMessage, intermediateSteps);

        return Arrays.asList(agentAction);
    }

    @Override
    public List<String> inputKeys() {
        return Arrays.asList("input");
    }

    @Override
    public List<String> getAllowedTools() {
        return Optional.ofNullable(this.getTools()).orElse(new ArrayList<>()).stream().map(BaseTool::getName).collect(Collectors.toList());
    }


    public List<FunctionDescription> getFunctions() {

        return Optional.ofNullable(this.getTools()).orElse(new ArrayList<>()).stream().map(FunctionDescription::convert).collect(Collectors.toList());
    }


    public static BasePromptTemplate createPrompt(SystemMessage systemMessage, List<BaseMessagePromptTemplate> extraPromptMessages) {

        List<BaseMessagePromptTemplate> promptTemplates = new ArrayList<>();

        promptTemplates.add(SystemMessagePromptTemplate.fromTemplate(systemMessage.getContent()));
        promptTemplates.addAll(extraPromptMessages);

        promptTemplates.add(HumanMessagePromptTemplate.fromTemplate("{input}"));
        promptTemplates.add(new MessagesPlaceholder("agent_scratchpad"));

        return ChatPromptTemplate.fromMessages(promptTemplates);
    }


    protected List<BaseMessage> formatIntermediateSteps(List<AgentAction> intermediateSteps) {

        List<BaseMessage> messages = new ArrayList<>();
        Optional.ofNullable(intermediateSteps).orElse(new ArrayList<>()).forEach(agentAction -> {
            messages.addAll(convertAgentActionToMessages(agentAction, agentAction.getObservation()));
        });

        return messages;
    }

    protected <R> List<BaseMessage> convertAgentActionToMessages(AgentAction agentAction, R observation) {

        List<BaseMessage> messages = new ArrayList<>();
        if (agentAction instanceof FunctionsAgentAction) {
            List<BaseMessage> history = ((FunctionsAgentAction) agentAction).getMessagesLog();
            messages.addAll(history);
            messages.add(createFunctionMessage(agentAction, observation));
        } else {
            messages = Arrays.asList(new AIMessage(agentAction.getLog()));
        }

        return messages;
    }

    protected FunctionMessage createFunctionMessage(AgentAction agentAction, Object observation) {

        return new FunctionMessage(((FunctionsAgentAction) agentAction).getTool(), String.valueOf(observation));
    }

    /**
     * 解析llm返回结果，只有2种情况。
     * 1，直接执行结束，无需函数调用
     * 2，llm返回需要进行函数调用
     *
     * @param baseMessage
     * @return
     */
    protected static AgentAction parseAiMessage(BaseMessage baseMessage, List<AgentAction> intermediateSteps) {

        Assert.isInstanceOf(AIMessage.class, baseMessage, "Expected an AI message got");

        ChatFunctionCall functionCall = (ChatFunctionCall) baseMessage.getAdditionalArgs().getOrDefault("function_call", null);

        if (functionCall != null) {

            String functionName = functionCall.getName();
            JsonNode toolInput = functionCall.getArguments();

            String contentMsg = StrUtil.isNotBlank(baseMessage.getContent()) ? "responded: " + baseMessage.getContent() + "\n" : "";
            String log = "\nInvoking: `" + functionName + "` with `" + toolInput + "`\n" + contentMsg + "\n";

            FunctionsAgentAction functionsAgentAction = new FunctionsAgentAction(functionName, toolInput, log, Arrays.asList(baseMessage));
            return functionsAgentAction;

        } else {

            AgentFinish agentFinish = new AgentFinish(baseMessage.getContent());

            //获取上一步调用调用记录
            AgentAction lastAgentAction = CollectionUtil.getLast(intermediateSteps);
            if (lastAgentAction != null && lastAgentAction instanceof FunctionsAgentAction) {
                //函数调用的返回结果
                String log = ((FunctionsAgentAction) lastAgentAction).getTool() + " " + lastAgentAction.getObservation();
                agentFinish.setLog(log);

            } else {
                //如果为空，说明LLM直接返回了未触发fun
                agentFinish.setNoFunLLm(true);
            }

            agentFinish.setMessagesLog(Arrays.asList(baseMessage));

            return agentFinish;
        }

    }

}
