package com.starcloud.ops.llm.langchain.core.tools.exception;


public class ValueError extends RuntimeException {

    private String toolName;

    private Object call;

    public ValueError(String message, Throwable cause, String toolName, Object call) {
        super(message, cause);
        this.toolName = toolName;
        this.call = call;
    }
}
