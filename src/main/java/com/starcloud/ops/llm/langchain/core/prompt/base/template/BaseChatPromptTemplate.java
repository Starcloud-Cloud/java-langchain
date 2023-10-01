package com.starcloud.ops.llm.langchain.core.prompt.base.template;

import com.starcloud.ops.llm.langchain.core.prompt.base.ChatPromptValue;

import com.starcloud.ops.llm.langchain.core.prompt.base.PromptValue;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import com.starcloud.ops.llm.langchain.core.schema.prompt.BasePromptTemplate;

import java.util.List;
import java.util.Map;


public abstract class BaseChatPromptTemplate extends BasePromptTemplate {

    protected abstract List<? extends BaseMessage> formatMessage(List<BaseVariable> variables);

    @Override
    public String format(List<BaseVariable> variables) {

        return this.formatPrompt(variables).toStr();
    }

    @Override
    public ChatPromptValue formatPrompt(List<BaseVariable> variables) {

        List<? extends BaseMessage> messages = this.formatMessage(variables);

        return new ChatPromptValue(messages);
    }

}
