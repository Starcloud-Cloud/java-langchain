package com.starcloud.ops.llm.langchain.core.agent;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.llm.langchain.core.agent.base.action.AgentAction;
import com.starcloud.ops.llm.langchain.core.agent.base.action.AgentFinish;
import com.starcloud.ops.llm.langchain.core.agent.base.BaseSingleActionAgent;
import com.starcloud.ops.llm.langchain.core.agent.base.action.FunctionsAgentAction;
import com.starcloud.ops.llm.langchain.core.callbacks.CallbackManager;
import com.starcloud.ops.llm.langchain.core.callbacks.StdOutCallbackHandler;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;

import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMUsage;
import com.starcloud.ops.llm.langchain.core.prompt.base.HumanMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.MessagesPlaceholder;
import com.starcloud.ops.llm.langchain.core.prompt.base.PromptValue;
import com.starcloud.ops.llm.langchain.core.prompt.base.SystemMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.*;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.BaseLanguageModel;
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

    private static final String TEMP_VARIABLE_SCRATCHPAD = "agent_scratchpad";

    private BaseLanguageModel llm;

    private List<BaseTool> tools;

    private BasePromptTemplate promptTemplate;

    private BaseCallbackManager callbackManager;

    private OpenAIFunctionsAgent(BaseLanguageModel llm, List<BaseTool> tools, BasePromptTemplate promptTemplate) {
        super();
        this.llm = llm;
        this.tools = tools;
        this.promptTemplate = promptTemplate;
    }

    public static OpenAIFunctionsAgent fromLLMAndTools(BaseLanguageModel llm, List<BaseTool> tools) {

        return fromLLMAndTools(llm, tools, new CallbackManager().addCallbackHandler(new StdOutCallbackHandler()), new ArrayList<>(), new SystemMessage("You are a helpful AI assistant."));
    }


    public static OpenAIFunctionsAgent fromLLMAndTools(BaseLanguageModel llm, List<BaseTool> tools, BaseCallbackManager callbackManager, List<BaseMessagePromptTemplate> extraPromptMessages, SystemMessage systemMessage) {

        Assert.isInstanceOf(ChatOpenAI.class, llm, "Only supported with ChatOpenAI models.");

        Optional.ofNullable(tools).orElse(new ArrayList<>()).forEach(tool -> {
            tool.setCallbackManager(callbackManager);
        });

        OpenAIFunctionsAgent agent = new OpenAIFunctionsAgent(llm, tools, createPrompt(systemMessage, extraPromptMessages));
        agent.setCallbackManager(callbackManager);

        return agent;
    }


    @Override
    public List<AgentAction> plan(List<AgentAction> intermediateSteps, List<BaseVariable> variables, BaseCallbackManager callbackManager) {

        List<BaseMessage> chatMessages = this.formatIntermediateSteps(intermediateSteps);

        List<BaseVariable> selectedInputs = Optional.ofNullable(variables).orElse(new ArrayList<>()).stream().filter(baseVariable -> !baseVariable.getField().equals(TEMP_VARIABLE_SCRATCHPAD)).collect(Collectors.toList());

        selectedInputs.add(BaseVariable.newObject(TEMP_VARIABLE_SCRATCHPAD, chatMessages));

        PromptValue promptValue = this.promptTemplate.formatPrompt(selectedInputs);

        List<? extends BaseMessage> messages = promptValue.toMessage();

        BaseMessage predictedMessage = this.llm.predictMessages(messages, null, this.getFunctions(), callbackManager);

        AgentAction agentAction = parseAiMessage(predictedMessage);

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

        return new FunctionMessage(agentAction.getTool(), observation.toString());
    }

    protected static AgentAction parseAiMessage(BaseMessage baseMessage) {

        Assert.isInstanceOf(AIMessage.class, baseMessage, "Expected an AI message got");

        ChatFunctionCall functionCall = (ChatFunctionCall) baseMessage.getAdditionalArgs().getOrDefault("function_call", null);

        BaseLLMUsage usage = (BaseLLMUsage) baseMessage.getAdditionalArgs().getOrDefault("usage", null);

        if (functionCall != null) {

            String functionName = functionCall.getName();
            JsonNode toolInput = functionCall.getArguments();

            String contentMsg = StrUtil.isNotBlank(baseMessage.getContent()) ? "responded: " + baseMessage.getContent() + "\n" : "";
            String log = "\nInvoking: `" + functionName + "` with `" + toolInput + "`\n" + contentMsg + "\n";

            FunctionsAgentAction functionsAgentAction = new FunctionsAgentAction(functionName, toolInput, log, Arrays.asList(baseMessage));

            functionsAgentAction.setUsage(usage);
            return functionsAgentAction;

        } else {

            return new AgentFinish(baseMessage.getContent(), baseMessage.getContent(), usage);
        }

    }

}
