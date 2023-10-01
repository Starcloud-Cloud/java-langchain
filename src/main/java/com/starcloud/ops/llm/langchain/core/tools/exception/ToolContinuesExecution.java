package com.starcloud.ops.llm.langchain.core.tools.exception;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;


/**
 * 工具发送的异常
 * 1，封装返回给llm 的内容，让llm继续下面的对话
 */
@Slf4j
@Data
public abstract class ToolContinuesExecution extends RuntimeException {

    private String toolName;

    private Object toolInput;

    private Integer errorCode;

    public ToolContinuesExecution(String message, Throwable cause) {
        super(message, cause);
    }

    public ToolContinuesExecution() {
    }

    public abstract String getObservation();

}
