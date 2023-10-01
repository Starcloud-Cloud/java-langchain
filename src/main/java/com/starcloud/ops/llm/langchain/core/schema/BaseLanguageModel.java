package com.starcloud.ops.llm.langchain.core.schema;

import com.knuddels.jtokkit.api.ModelType;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.PromptValue;
import com.starcloud.ops.llm.langchain.core.callbacks.BaseCallbackManager;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import com.starcloud.ops.llm.langchain.core.schema.tool.FunctionDescription;
import com.starcloud.ops.llm.langchain.core.utils.TokenUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;


@Slf4j
@Data
public abstract class BaseLanguageModel<R> {

    public abstract String getModelType();

    public abstract BaseLLMResult<R> generatePrompt(List<PromptValue> promptValues);

    public Long getNumTokens(String text) {
        return TokenUtils.tokens(ModelType.GPT_3_5_TURBO, text);
    }


    public Long getNumTokensFromMessages(List<BaseMessage> messages) {
        return Optional.ofNullable(messages).orElse(new ArrayList<>()).stream().map((message) -> this.getNumTokens(message.getContent())).reduce(0L, Long::sum);
    }

    public abstract void setVerbose(Boolean verbose);

    public String predict(String text) {
        return this.predict(text, null);
    }

    public abstract String predict(String text, List<String> stops);

    public abstract BaseMessage predictMessages(List<BaseMessage> baseMessages);

    @Deprecated
    public abstract BaseMessage predictMessages(List<BaseMessage> baseMessages, List<String> stops);

    public abstract BaseMessage predictMessages(List<BaseMessage> baseMessages, List<String> stops, List<FunctionDescription> functionDescriptions, BaseCallbackManager callbackManager);

}

