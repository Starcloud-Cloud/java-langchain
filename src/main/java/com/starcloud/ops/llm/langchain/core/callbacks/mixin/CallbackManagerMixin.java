package com.starcloud.ops.llm.langchain.core.callbacks.mixin;


public interface CallbackManagerMixin {

    <T> T onLLMStart(Object... objects);

    default  <T> T onChatModelStart(Object... objects) {
        return null;
    }

    default void onRetrieverStart(Object... objects) {
        return;
    }

    <T> T onChainStart(Object... objects);

    <T> T onToolStart(Object... objects);

}
