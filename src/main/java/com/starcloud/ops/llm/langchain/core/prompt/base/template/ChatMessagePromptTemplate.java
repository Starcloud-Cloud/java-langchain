package com.starcloud.ops.llm.langchain.core.prompt.base.template;

import com.starcloud.ops.llm.langchain.core.prompt.base.AIMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.StringPromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.ChatMessage;

import java.util.List;

public class ChatMessagePromptTemplate extends BaseStringMessagePromptTemplate {

    private String role;

    public ChatMessagePromptTemplate(StringPromptTemplate promptTemplate, String role) {
        super(promptTemplate);
        this.role = role;
    }

    @Override
    public BaseMessage format(List<BaseVariable> variables) {

        String text = this.getPromptTemplate().format(variables);
        return new ChatMessage(text, this.role);
    }

}
