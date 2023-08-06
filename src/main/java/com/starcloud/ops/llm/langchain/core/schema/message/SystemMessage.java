package com.starcloud.ops.llm.langchain.core.schema.message;

public class SystemMessage extends BaseMessage {

    public SystemMessage(String content) {
        super(content);
    }

    @Override
    public String getType() {

        return "system";
    }
}
