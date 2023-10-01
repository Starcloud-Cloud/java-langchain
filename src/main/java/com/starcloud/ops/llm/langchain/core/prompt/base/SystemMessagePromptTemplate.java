package com.starcloud.ops.llm.langchain.core.prompt.base;

import com.starcloud.ops.llm.langchain.core.prompt.base.template.BaseMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.BaseStringMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.PromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.SystemMessage;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SystemMessagePromptTemplate extends BaseStringMessagePromptTemplate {

    public SystemMessagePromptTemplate(StringPromptTemplate promptTemplate) {
        super(promptTemplate);
    }

    public static SystemMessagePromptTemplate fromTemplate(String... params) {

        StringPromptTemplate promptTemplate = PromptTemplate.fromTemplate( params);
        return new SystemMessagePromptTemplate(promptTemplate);
    }

    @Override
    public BaseMessage format(List<BaseVariable> variables) {

        String text = this.getPromptTemplate().format(variables);
        return new SystemMessage(text);
    }
}
