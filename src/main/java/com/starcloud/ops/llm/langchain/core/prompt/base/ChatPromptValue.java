package com.starcloud.ops.llm.langchain.core.prompt.base;


import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import lombok.Data;

import java.util.List;


@Data
public class ChatPromptValue extends PromptValue {


    private List<BaseMessage> messages;

    public ChatPromptValue(List<BaseMessage> messages) {
        this.messages = messages;
    }

    @Override
    public String toStr() {
        return this.messages.get(0).getContent();
    }

    @Override
    public List<BaseMessage> toMessage() {
        return this.messages;
    }
}
