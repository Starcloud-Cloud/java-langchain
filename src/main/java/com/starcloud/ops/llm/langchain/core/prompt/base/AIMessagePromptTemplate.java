package com.starcloud.ops.llm.langchain.core.prompt.base;

import com.starcloud.ops.llm.langchain.core.prompt.base.StringPromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.BaseMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.BaseStringMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.PromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.message.AIMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.HumanMessage;

import java.util.List;

public class AIMessagePromptTemplate extends BaseStringMessagePromptTemplate {

    public AIMessagePromptTemplate(StringPromptTemplate promptTemplate) {
        super(promptTemplate);
    }

    public static BaseMessagePromptTemplate fromTemplate(String... params) {

        StringPromptTemplate promptTemplate = PromptTemplate.fromTemplate(params);
        return new AIMessagePromptTemplate(promptTemplate);
    }


    @Override
    public BaseMessage format(List<BaseVariable> variables) {

        String text = this.getPromptTemplate().format(variables);
        return new AIMessage(text);
    }
}
