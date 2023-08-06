package com.starcloud.ops.llm.langchain.core.callbacks.mixin;

public interface ChainManagerMixin {

    default void onChainEnd(Object... objects) {
    }


    default void onChainError(String message, Throwable throwable) {
    }


    default void onAgentAction(Object... objects) {
    }

    default void onAgentFinish(Object... objects) {
    }


}
