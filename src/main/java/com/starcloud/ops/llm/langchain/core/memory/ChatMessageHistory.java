package com.starcloud.ops.llm.langchain.core.memory;

import cn.hutool.core.collection.CollectionUtil;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.AIMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.FunctionMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.HumanMessage;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author df007df
 */
@Data
public class ChatMessageHistory {

    private List<BaseMessage> messages = new ArrayList<>();

    public void addMessage(BaseMessage baseMessage) {
        this.messages.add(baseMessage);
    }

    public void addUserMessage(String content) {
        this.messages.add(new HumanMessage(content));
    }

    public void addAiMessage(String content) {

        this.messages.add(new AIMessage(content));
    }


    public void addFunMessage(String name, Object arguments) {

        this.messages.add(new FunctionMessage(name, arguments));
    }

    public List<BaseMessage> limitMessage(long limit) {

        if (limit >= 0) {
            return Optional.ofNullable(this.messages).orElse(new ArrayList<>()).stream().limit(limit).collect(Collectors.toList());
        } else {
            return Optional.ofNullable(this.messages).orElse(new ArrayList<>()).stream().skip(CollectionUtil.size(this.messages) + limit).collect(Collectors.toList());
        }

    }

}
