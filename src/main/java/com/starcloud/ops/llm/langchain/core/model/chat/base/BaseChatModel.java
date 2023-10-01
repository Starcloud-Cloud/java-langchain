package com.starcloud.ops.llm.langchain.core.model.chat.base;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import cn.hutool.core.collection.CollectionUtil;
import com.starcloud.ops.llm.langchain.core.callbacks.*;
import com.starcloud.ops.llm.langchain.core.model.llm.LLMUtils;
import com.starcloud.ops.llm.langchain.core.model.llm.base.*;
import com.starcloud.ops.llm.langchain.core.prompt.base.ChatPromptValue;
import com.starcloud.ops.llm.langchain.core.prompt.base.PromptValue;
import com.starcloud.ops.llm.langchain.core.schema.BaseLanguageModel;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.HumanMessage;
import com.starcloud.ops.llm.langchain.core.schema.tool.FunctionDescription;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Data
public abstract class BaseChatModel<R> extends BaseLanguageModel<R> {

    private static final Logger logger = LoggerFactory.getLogger(BaseChatModel.class);

    private Boolean verbose = false;

    public Boolean getVerbose() {
        return verbose;
    }

    private Boolean cache;

    private BaseCallbackManager callbackManager = new CallbackManager();

    public void addCallbackHandler(BaseCallbackHandler callbackHandler) {
        this.callbackManager.addCallbackHandler(callbackHandler);
    }

    public BaseCallbackManager getCallbackManager() {
        return callbackManager;
    }


    @Override
    public void setVerbose(Boolean verbose) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger(BaseChatModel.class).setLevel(Level.DEBUG);
        this.verbose = verbose;
    }

    @Override
    public String predict(String text, List<String> stops) {

        HumanMessage message = new HumanMessage(text);
        return this.call(Arrays.asList(message), stops);
    }

    protected abstract ChatResult<R> _generate(List<BaseMessage> chatMessages, List<String> stops, List<FunctionDescription> functions, CallbackManagerForLLMRun callbackManager);


    @Override
    public BaseMessage predictMessages(List<BaseMessage> baseMessages) {
        return this.predictMessages(baseMessages, null);
    }

    @Override
    public BaseMessage predictMessages(List<BaseMessage> baseMessages, List<String> stops) {
        ChatResult<R> chatResult = this.generate(Arrays.asList(baseMessages), stops);
        return chatResult.getChatGenerations().get(0).getChatMessage();
    }

    @Override
    public BaseMessage predictMessages(List<BaseMessage> baseMessages, List<String> stops, List<FunctionDescription> functionDescriptions, BaseCallbackManager callbackManager) {

        this.setCallbackManager(callbackManager);
        ChatResult<R> chatResult = this.generate(Arrays.asList(baseMessages), null, functionDescriptions);

        BaseMessage baseMessage = chatResult.getChatGenerations().get(0).getChatMessage();
        baseMessage.getAdditionalArgs().put("usage", chatResult.getUsage());

        return baseMessage;
    }

    public ChatResult<R> generate(List<List<BaseMessage>> chatMessages, List<String> stops, List<FunctionDescription> functions) {

        List<CallbackManagerForLLMRun> llmRuns = this.getCallbackManager().onChatModelStart(this.getClass(), chatMessages);

        log.debug("BaseChatModel.generate: {}", chatMessages);

        List<ChatResult<R>> chatResults = new ArrayList<>();

        for (int i = 0; i < CollectionUtil.size(chatMessages); i++) {

            CallbackManagerForLLMRun llmRun = llmRuns.get(i);

            try {

                llmRun.onLLMStart(this.getClass(), chatMessages.get(i), stops, functions);

                ChatResult<R> chatResult = this._generate(chatMessages.get(i), stops, functions, llmRun);
                chatResults.add(chatResult);

                llmRun.onLLMEnd(this.getClass(), chatResult.getText(), chatResult.getUsage());

                //llmRun.onLLMEnd();

            } catch (Exception e) {

                llmRun.onLLMError(e.getMessage(), e);

            }
        }

        log.debug("BaseChatModel.generate result: {}", chatResults);

//        this.getCallbackManager().onChatModelEnd(this.getClass(), chatResults);

        return this.combineLLMOutputs(chatResults);
    }


    @Override
    public BaseLLMResult<R> generatePrompt(List<PromptValue> promptValues) {

        //@todo 结构不对多余的转换
        List<List<BaseMessage>> baseMessages = Optional.ofNullable(promptValues).orElse(new ArrayList<>()).stream().map((PromptValue::toMessage)).collect(Collectors.toList());

        ChatResult<R> chatResult = this.generate(baseMessages, null);

        return BaseLLMResult.data(chatResult.getChatGenerations(), chatResult.getUsage());
    }

    public String call(ChatPromptValue chatPromptValue) {
        ChatResult<R> chatResult = this.generate(Arrays.asList(chatPromptValue.toMessage()), null);
        return chatResult.getChatGenerations().get(0).getText();
    }

    public String call(List<BaseMessage> chatMessages) {
        ChatResult<R> chatResult = this.generate(Arrays.asList(chatMessages), null);
        return chatResult.getChatGenerations().get(0).getText();
    }

    public String call(List<BaseMessage> chatMessages, List<String> stops) {
        ChatResult<R> chatResult = this.generate(Arrays.asList(chatMessages), stops);
        return chatResult.getChatGenerations().get(0).getText();
    }

    public String call(List<BaseMessage> chatMessages, List<String> stops, List<FunctionDescription> functionDescriptions) {
        ChatResult<R> chatResult = this.generate(Arrays.asList(chatMessages), stops, functionDescriptions);
        return chatResult.getChatGenerations().get(0).getText();
    }


    protected ChatResult combineLLMOutputs(List<ChatResult<R>> chatResults) {

        List<BaseLLMUsage> baseLLMUsageList = Optional.ofNullable(chatResults).orElse(new ArrayList<>()).stream().map(ChatResult::getUsage).collect(Collectors.toList());
        BaseLLMUsage baseLLMUsage = LLMUtils.combineBaseLLMUsage(baseLLMUsageList);

        List<ChatGeneration<R>> generations = Optional.ofNullable(chatResults).orElse(new ArrayList<>()).stream().filter((chatResult) -> CollectionUtil.isNotEmpty(chatResult.getChatGenerations())).flatMap((chatResult) -> chatResult.getChatGenerations().stream()).collect(Collectors.toList());

        return ChatResult.data(generations, baseLLMUsage);

    }

    public ChatResult<R> generate(List<List<BaseMessage>> chatMessages, List<String> stops) {

        return this.generate(chatMessages, stops, null);
    }

    public ChatResult<R> generate(List<List<BaseMessage>> chatMessages) {

        return this.generate(chatMessages, null, null);
    }
}
