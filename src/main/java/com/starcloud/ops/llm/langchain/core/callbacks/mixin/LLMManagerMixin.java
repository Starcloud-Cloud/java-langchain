package com.starcloud.ops.llm.langchain.core.callbacks.mixin;

public interface LLMManagerMixin {


    default void onLLMNewToken(Object... objects) {
    }

    default void onLLMEnd(Object... objects) {
    }


    default void onLLMError(String message) {
    }

    default void onLLMError(String message, Throwable throwable) {
    }
}
