package com.starcloud.ops.llm.langchain.core.chain.conversation;

import com.starcloud.ops.llm.langchain.core.chain.LLMChain;
import com.starcloud.ops.llm.langchain.core.memory.BaseMemory;
import com.starcloud.ops.llm.langchain.core.memory.buffer.ConversationBufferMemory;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLM;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.PromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.BaseLanguageModel;
import com.starcloud.ops.llm.langchain.core.schema.prompt.BasePromptTemplate;
import lombok.EqualsAndHashCode;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author df007df
 */
@EqualsAndHashCode(callSuper = true)
public class ConversationChain<R> extends LLMChain<R> {

    private static final String DEFAULT_TEMPLATE = "The following is a friendly conversation between a human and an AI. The AI is talkative and provides lots of specific details from its context. If the AI does not know the answer to a question, it truthfully says it does not know." +
            "\n" +
            "Current conversation:\n" +
            "{history}\n" +
            "Human: {input}\n" +
            "AI:";

    public static BasePromptTemplate basePromptTemplate = new PromptTemplate(DEFAULT_TEMPLATE, Arrays.asList(BaseVariable.newString("history"), BaseVariable.newString("input")));


    public ConversationChain(BaseLanguageModel<R> llm) {
        super(llm, basePromptTemplate);
        this.setMemory(new ConversationBufferMemory());
    }

    public ConversationChain(BaseLanguageModel<R> llm, BaseMemory baseMemory) {
        super(llm, basePromptTemplate);
        this.setMemory(Optional.ofNullable(baseMemory).orElse(null));
    }
}
