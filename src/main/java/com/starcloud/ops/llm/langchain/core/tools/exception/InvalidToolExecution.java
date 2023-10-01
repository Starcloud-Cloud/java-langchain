package com.starcloud.ops.llm.langchain.core.tools.exception;

import lombok.extern.slf4j.Slf4j;


/**
 * 工具发送的异常
 * 1，封装返回给llm 的内容，让llm继续下面的对话
 */
@Slf4j
public class InvalidToolExecution extends ToolContinuesExecution {

    public InvalidToolExecution(String toolName, Object toolInput) {
        this.setToolName(toolName);
        this.setToolInput(toolInput);
        this.setErrorCode(-9);
    }

    @Override
    public String getObservation() {
        return this.getToolName() + " is not a valid tool, try another one.";
    }
}
