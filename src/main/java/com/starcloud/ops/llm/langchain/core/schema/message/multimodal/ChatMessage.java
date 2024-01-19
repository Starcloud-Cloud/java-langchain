package com.starcloud.ops.llm.langchain.core.schema.message.multimodal;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ChatMessage extends MultiModalMessage {

    private String role;

    public ChatMessage(List<Map<String, Object>> content, String role) {
        super(content);
        this.role = role;
    }


    @Override
    public String getType() {

        return "chat";
    }
}
