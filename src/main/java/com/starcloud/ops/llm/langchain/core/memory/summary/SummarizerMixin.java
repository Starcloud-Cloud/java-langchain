package com.starcloud.ops.llm.langchain.core.memory.summary;

import cn.hutool.core.util.ClassUtil;
import com.starcloud.ops.llm.langchain.core.chain.LLMChain;
import com.starcloud.ops.llm.langchain.core.memory.BaseChatMemory;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLM;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.PromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.BaseLanguageModel;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.SystemMessage;
import com.starcloud.ops.llm.langchain.core.schema.prompt.BasePromptTemplate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;

import java.util.Arrays;
import java.util.List;

@Data
public abstract class SummarizerMixin extends BaseChatMemory {

    private String humanPrefix;

    private String aiPrefix;

    private BaseLanguageModel llm;

    private Class<? extends BaseMessage> summaryMessageCls = SystemMessage.class;

    private static final String DEFAULT_SUMMARIZER_TEMPLATE = "Progressively summarize the lines of conversation provided, adding onto the previous summary returning a new summary." +
            "\n" +
            "EXAMPLE\n" +
            "Current summary:\n" +
            "The human asks what the AI thinks of artificial intelligence. The AI thinks artificial intelligence is a force for good.\n" +
            "\n" +
            "New lines of conversation:\n" +
            "Human: Why do you think artificial intelligence is a force for good?\n" +
            "AI: Because artificial intelligence will help humans reach their full potential.\n" +
            "\n" +
            "New summary:\n" +
            "The human asks what the AI thinks of artificial intelligence. The AI thinks artificial intelligence is a force for good because it will help humans reach their full potential.\n" +
            "END OF EXAMPLE\n" +
            "\n" +
            "Current summary:\n" +
            "{summary}\n" +
            "\n" +
            "New lines of conversation:\n" +
            "{new_lines}\n" +
            "\n" +
            "New summary:";


    private BasePromptTemplate prompt;


    public SummarizerMixin() {

        super();
        this.prompt = new PromptTemplate(DEFAULT_SUMMARIZER_TEMPLATE, Arrays.asList(
                BaseVariable.newString("summary"),
                BaseVariable.newString("new_lines")
        ));

    }

    public SummarizerMixin(BaseLanguageModel llm) {
        super();
        this.prompt = new PromptTemplate(DEFAULT_SUMMARIZER_TEMPLATE, Arrays.asList(
                BaseVariable.newString("summary"),
                BaseVariable.newString("new_lines")
        ));

        this.llm = llm;
    }

    @SneakyThrows
    protected BaseMessage createSummaryMessage(String text) {
        Class c = Class.forName(this.summaryMessageCls.getName());
        BaseMessage message = (BaseMessage) c.getConstructor(String.class).newInstance(text);

        return message;
    }

    protected BaseLLMResult predictNewSummary(List<BaseMessage> messages, String existingSummary) {

        String newLines = BaseMessage.getBufferString(messages);

        LLMChain<BaseLLMResult> llmChain = new LLMChain(this.llm, this.prompt);

        return llmChain.call(Arrays.asList(
                BaseVariable.newString("new_lines", newLines),
                BaseVariable.newString("summary", existingSummary)
        ));
    }


}
