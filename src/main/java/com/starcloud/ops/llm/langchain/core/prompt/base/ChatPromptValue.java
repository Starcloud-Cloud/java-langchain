package com.starcloud.ops.llm.langchain.core.prompt.base;


import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;


@Data
public class ChatPromptValue extends PromptValue {


    private List<? extends BaseMessage> messages;

    public ChatPromptValue(List<? extends BaseMessage> messages) {
        this.messages = messages;
    }

    @Override
    public String toStr() {
        return this.messages.get(0).getContent();
    }

    @Override
    public List<? extends BaseMessage> toMessage() {
        return this.messages;
    }
}
