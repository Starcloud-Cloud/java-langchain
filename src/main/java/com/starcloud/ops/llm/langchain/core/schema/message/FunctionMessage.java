package com.starcloud.ops.llm.langchain.core.schema.message;

import lombok.Data;

import java.util.HashMap;

@Data
public class FunctionMessage extends BaseMessage {

    private String name;

    public FunctionMessage(String name, String content) {
        super(content);
        this.name = name;
    }

    public FunctionMessage(String name, Object arguments) {
        super("");
        this.name = name;
        this.setAdditionalArgs(new HashMap() {{
            put("arguments", arguments);
        }});
    }

    @Override
    public String getType() {

        return "function";
    }
}
