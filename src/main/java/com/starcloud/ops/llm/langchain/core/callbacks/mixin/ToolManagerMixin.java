package com.starcloud.ops.llm.langchain.core.callbacks.mixin;

public interface ToolManagerMixin {


    default void onToolEnd(Object... objects) {
    }

    default void onToolError(String message, Throwable throwable) {
    }

}
