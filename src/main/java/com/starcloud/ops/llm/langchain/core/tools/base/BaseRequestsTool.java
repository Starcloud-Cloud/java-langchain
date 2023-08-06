package com.starcloud.ops.llm.langchain.core.tools.base;

@Deprecated
public interface BaseRequestsTool {

    default String get(String url) {
        return "";
    }
}
