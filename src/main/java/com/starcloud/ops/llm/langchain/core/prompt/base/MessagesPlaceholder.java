package com.starcloud.ops.llm.langchain.core.prompt.base;

import com.starcloud.ops.llm.langchain.core.prompt.base.template.BaseMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class MessagesPlaceholder extends BaseMessagePromptTemplate {

    private String variableName;

    public MessagesPlaceholder(String variableName) {
        this.variableName = variableName;
    }

    @Override
    public List<BaseMessage> formatMessages(List<BaseVariable> variables) {

        return (List<BaseMessage>) Optional.ofNullable(variables).orElse(new ArrayList<>()).stream().filter((baseVariable) -> {
            return baseVariable.getField().equals(this.variableName);
        }).findFirst().map(BaseVariable::getValue).orElse(new ArrayList<>());

    }
}
