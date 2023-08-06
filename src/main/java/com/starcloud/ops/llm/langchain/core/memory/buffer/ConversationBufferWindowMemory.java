package com.starcloud.ops.llm.langchain.core.memory.buffer;


import cn.hutool.core.collection.CollectionUtil;
import com.starcloud.ops.llm.langchain.core.memory.BaseChatMemory;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.*;
import java.util.stream.Collectors;


/**
 * @author df007df
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ConversationBufferWindowMemory extends BaseChatMemory {

    @Builder.Default
    private Integer k = 6;

    public ConversationBufferWindowMemory(Integer k) {
        this.k = k;
    }

    @Override
    public List<BaseVariable> loadMemoryVariables() {

        List<BaseMessage> messages = getChatHistory().getMessages();
        messages = Optional.ofNullable(messages).orElse(new ArrayList<>()).stream().skip(CollectionUtil.size(messages) - this.k * 2).collect(Collectors.toList());
        return Arrays.asList(BaseVariable.builder()
                .field(MEMORY_KEY)
                .value(BaseMessage.getBufferString(messages))
                .build());
    }


}
