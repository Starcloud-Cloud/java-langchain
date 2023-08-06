package com.starcloud.ops.llm.langchain.core.memory.summary;

import cn.hutool.core.collection.CollectionUtil;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLM;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseOpenAI;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;

import java.util.Collections;
import java.util.List;


/**
 * @author df007df
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ConversationSummaryMemory extends SummarizerMixin {

    private String buffer;

    public ConversationSummaryMemory() {
        super();
    }

    public ConversationSummaryMemory(BaseLLM llm) {
        super(llm);
    }

    @Override
    public void saveContext(List<BaseVariable> baseVariables, BaseLLMResult result) {

        super.saveContext(baseVariables, result);

        List<BaseMessage> messages = this.getChatHistory().limitMessage(-2);

        if (CollectionUtil.isNotEmpty(messages)) {
            BaseLLMResult baseLLMResult = this.predictNewSummary(messages, this.getBuffer());
            this.setBuffer(baseLLMResult.getText());
        }
    }

    @SneakyThrows
    @Override
    public List<BaseVariable> loadMemoryVariables() {

        if (this.getReturnMessages()) {

            BaseMessage message = this.createSummaryMessage(this.buffer);

            return Collections.singletonList(BaseVariable.builder()
                    .field(MEMORY_KEY)
                    .value(message)
                    .build());
        } else {
            return Collections.singletonList(BaseVariable.builder()
                    .field(MEMORY_KEY)
                    .value(this.buffer)
                    .build());

        }
    }


}
