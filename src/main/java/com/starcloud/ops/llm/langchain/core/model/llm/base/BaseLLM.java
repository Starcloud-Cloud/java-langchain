package com.starcloud.ops.llm.langchain.core.model.llm.base;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import cn.hutool.core.collection.CollectionUtil;
import com.starcloud.ops.llm.langchain.core.callbacks.BaseCallbackManager;
import com.starcloud.ops.llm.langchain.core.callbacks.CallbackManager;
import com.starcloud.ops.llm.langchain.core.callbacks.CallbackManagerForLLMRun;
import com.starcloud.ops.llm.langchain.core.prompt.base.PromptValue;
import com.starcloud.ops.llm.langchain.core.schema.BaseLanguageModel;
import com.starcloud.ops.llm.langchain.core.schema.message.AIMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import com.starcloud.ops.llm.langchain.core.schema.tool.FunctionDescription;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Data
public abstract class BaseLLM<R> extends BaseLanguageModel<R> {

    private static final Logger logger = LoggerFactory.getLogger(BaseLLM.class);

    private Boolean cache;

    private BaseCallbackManager callbackManager = new CallbackManager();

    public BaseCallbackManager getCallbackManager() {
        return callbackManager;
    }

    public void setCallbackManager(BaseCallbackManager callbackManager) {
        this.callbackManager = callbackManager;
    }


    private Boolean verbose = false;

    public Boolean getVerbose() {
        return verbose;
    }

    @Override
    public void setVerbose(Boolean verbose) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger(BaseLLM.class).setLevel(Level.DEBUG);
        this.verbose = verbose;
    }


    protected abstract BaseLLMResult<R> _generate(List<String> texts, CallbackManagerForLLMRun callbackManager);


    protected BaseLLMResult<R> _agenerate(List<String> texts) {
        return null;
    }


    @Override
    public BaseLLMResult<R> generatePrompt(List<PromptValue> promptValues) {

        return this.generate(Optional.ofNullable(promptValues).orElse(new ArrayList<>()).stream().map(PromptValue::toStr).collect(Collectors.toList()));
    }

    public BaseLLMResult<R> generate(List<String> prompts) {

        logger.debug("BaseLLM.generate: {}", prompts);

        List<CallbackManagerForLLMRun> llmRuns = this.getCallbackManager().onLLMStart(this.getClass().getSimpleName(), prompts);

        List<BaseLLMResult<R>> chatResults = new ArrayList<>();

        for (int i = 0; i < CollectionUtil.size(prompts); i++) {

            CallbackManagerForLLMRun llmRun = llmRuns.get(i);

            try {
                //this.isLLMCache()

                chatResults.add(this._generate(Arrays.asList(prompts.get(i)), llmRun));

                llmRun.onLLMEnd(this.getClass().getSimpleName(), chatResults.get(0));

            } catch (Exception e) {

                llmRun.onLLMError(e.getMessage(), e);

                log.error("BaseLLm generate is fail: {}", e.getMessage(), e);

                throw e;

            }
        }


        return chatResults.get(0);
    }


    @Override
    public String predict(String text, List<String> stops) {
        return this.call(text);
    }

    @Override
    public BaseMessage predictMessages(List<BaseMessage> baseMessages) {
        return this.predictMessages(baseMessages, null, null, null);
    }

    @Override
    public BaseMessage predictMessages(List<BaseMessage> baseMessages, List<String> stops, List<FunctionDescription> functionDescriptions, BaseCallbackManager callbackManager) {

        if (callbackManager != null) {
            this.setCallbackManager(callbackManager);
        }

        String content = this.predict(BaseMessage.getBufferString(baseMessages), stops);

        return new AIMessage(content);
    }


    public String call(String text) {
        BaseLLMResult<R> baseLLMResult = this.generate(Arrays.asList(text));
        return baseLLMResult.getText();
    }


    private Boolean isLLMCache() {
        return false;
    }

    private Map<String, List<BaseGeneration<R>>> getCachePrompts(List<String> promptValueList) {

        return null;
    }

    private Map updatePromptsCache(List<String> prompts, BaseLLMResult<R> baseLLMResult) {
        return baseLLMResult.getLlmOutput();
    }

    public void save(String path) {

    }

}

