package com.starcloud.ops.llm.langchain.core.memory.buffer;

import com.starcloud.ops.llm.langchain.core.memory.BaseChatMemory;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseOpenAI;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.BaseLanguageModel;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author df007df
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ConversationTokenBufferMemory extends BaseChatMemory {

    private Integer maxTokenLimit = 2000;

    private BaseLanguageModel llm;

    public ConversationTokenBufferMemory(BaseLanguageModel llm, Integer maxTokenLimit) {
        this.maxTokenLimit = maxTokenLimit;
        this.llm = llm;
    }

    protected List<BaseMessage> getBuffer() {
        return this.getChatHistory().getMessages();
    }


    @Override
    public List<BaseVariable> loadMemoryVariables() {

        if (this.getReturnMessages()) {

            return Collections.singletonList(BaseVariable.builder()
                    .field(MEMORY_KEY)
                    .value(this.getBuffer())
                    .build());
        } else {

            return Collections.singletonList(BaseVariable.builder()
                    .field(MEMORY_KEY)
                    .value(BaseMessage.getBufferString(this.getBuffer()))
                    .build());
        }

    }

    @Override
    public void saveContext(List<BaseVariable> baseVariables, BaseLLMResult result) {

        super.saveContext(baseVariables, result);

        List<BaseMessage> messages = this.getBuffer();

        Long sum = this.llm.getNumTokensFromMessages(messages);

        if (sum > this.maxTokenLimit) {

            List<BaseMessage> prunedMemory = new ArrayList<>();

            while (sum > this.maxTokenLimit) {
                prunedMemory.add(this.getBuffer().remove(0));
                sum = llm.getNumTokensFromMessages(this.getBuffer());
            }
        }
    }


}
