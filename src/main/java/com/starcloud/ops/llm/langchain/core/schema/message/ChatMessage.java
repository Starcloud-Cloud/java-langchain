package com.starcloud.ops.llm.langchain.core.schema.message;


import lombok.Data;

import java.util.Map;

@Data
public class ChatMessage extends BaseMessage {


    private String role;


    public ChatMessage(String content, String role) {
        super(content);
        this.role = role;
    }


    @Override
    public String getType() {

        return "chat";
    }
}
