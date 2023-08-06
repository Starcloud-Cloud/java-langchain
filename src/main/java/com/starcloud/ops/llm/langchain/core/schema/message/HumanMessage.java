package com.starcloud.ops.llm.langchain.core.schema.message;

public class HumanMessage extends BaseMessage {

    public HumanMessage(String content) {
        super(content);
    }

    @Override
    public String getType() {

        return "human";
    }
}
