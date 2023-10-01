package com.starcloud.ops.llm.langchain.core.tools.exception;

import lombok.extern.slf4j.Slf4j;


/**
 * 工具发送的异常
 * 1，封装返回给llm 的内容，让llm继续下面的对话
 */
@Slf4j
public class FailToolExecution extends ToolContinuesExecution {

    public FailToolExecution(String toolName, Object toolInput, Integer errorCode, String message) {
        super(message, null);
        this.setToolName(toolName);
        this.setToolInput(toolInput);
        this.setErrorCode(errorCode);
    }

    public FailToolExecution(String toolName, Object toolInput, Integer errorCode, String message, Throwable cause) {
        super(message, cause);
        this.setToolName(toolName);
        this.setToolInput(toolInput);
        this.setErrorCode(errorCode);
    }

    @Override
    public String getObservation() {
        return this.getToolName() + " call failed with no return.";
    }
}
