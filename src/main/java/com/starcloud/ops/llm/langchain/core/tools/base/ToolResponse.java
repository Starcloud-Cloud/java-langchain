package com.starcloud.ops.llm.langchain.core.tools.base;

import lombok.Data;


/**
 * 工具返回封装类
 *
 * @param <R>
 */
@Data
public class ToolResponse<R> {

    /**
     * 工具执行状态
     */
    private Boolean status;

    /**
     * 工具执行返回结果传递给LLM
     */
    private Object observation;

    /**
     * 工具执行扩展结果，供下游使用
     */
    private R response;

    private ToolResponse(Object observation) {
        this.observation = observation;
    }

    private ToolResponse() {
    }

    public static ToolResponse buildObservation(Object observation) {

        return new ToolResponse(observation);
    }

    public static <R> ToolResponse buildResponse(R response) {
        ToolResponse toolResponse = new ToolResponse();
        toolResponse.setResponse(response);
        return toolResponse;
    }
}
