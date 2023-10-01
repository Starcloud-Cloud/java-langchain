package com.starcloud.ops.llm.langchain.core.schema.message;

import lombok.Data;

import java.util.HashMap;

@Data
public class FunctionMessage extends BaseMessage {

    private String name;

    /**
     * fun执行状态
     */
    private Boolean status;

    /**
     * fun执行耗时
     */
    private Long elapsed;

    /**
     * 函数调用参数
     */
    private Object arguments;

    public FunctionMessage(String name, String content) {
        super(content);
        this.name = name;
    }

    public FunctionMessage(String name, Object arguments) {
        super("");
        this.name = name;
        this.arguments = arguments;
    }

    @Override
    public String getType() {

        return "function";
    }
}
