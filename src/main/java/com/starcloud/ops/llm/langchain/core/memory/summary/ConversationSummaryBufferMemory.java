package com.starcloud.ops.llm.langchain.core.memory.summary;

import cn.hutool.core.util.StrUtil;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLM;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.BaseLanguageModel;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import com.starcloud.ops.llm.langchain.core.utils.MessageConvert;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author df007df
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ConversationSummaryBufferMemory extends SummarizerMixin {

    private String movingSummaryBuffer = "";

    private Integer maxTokenLimit = 2000;

    public ConversationSummaryBufferMemory(BaseLanguageModel llm, Integer maxTokenLimit) {
        super(llm);
        this.maxTokenLimit = maxTokenLimit;
    }


    protected List<BaseMessage> getBuffer() {
        return this.getChatHistory().getMessages();
    }


    @SneakyThrows
    @Override
    public List<BaseVariable> loadMemoryVariables() {

        List<BaseMessage> messages = this.getBuffer();

        if (StrUtil.isNotBlank(this.movingSummaryBuffer)) {
            BaseMessage firstMessages = this.createSummaryMessage(this.movingSummaryBuffer);
            messages.add(0, firstMessages);
        }

        if (this.getReturnMessages()) {

            return Arrays.asList(BaseVariable.builder()
                    .field(MEMORY_KEY)
                    .value(messages)
                    .build());
        } else {
            return Arrays.asList(BaseVariable.builder()
                    .field(MEMORY_KEY)
                    .value(BaseMessage.getBufferString(messages))
                    .build());

        }
    }

    @Override
    public void saveContext(List<BaseVariable> baseVariables, BaseLLMResult result) {

        super.saveContext(baseVariables, result);

        List<BaseMessage> messages = this.getBuffer();

        Long sum = this.getLlm().getNumTokensFromMessages(messages);

        if (sum > this.maxTokenLimit) {

            List<BaseMessage> prunedMemory = new ArrayList<>();

            while (sum > this.maxTokenLimit) {

                prunedMemory.add(this.getBuffer().remove(0));
                sum = this.getLlm().getNumTokensFromMessages(this.getBuffer());
            }

            BaseLLMResult baseLLMResult = this.predictNewSummary(prunedMemory, this.movingSummaryBuffer);
            this.movingSummaryBuffer = baseLLMResult.getText();
        }
    }


}
