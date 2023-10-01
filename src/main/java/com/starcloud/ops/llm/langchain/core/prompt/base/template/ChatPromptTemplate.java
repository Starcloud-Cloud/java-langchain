package com.starcloud.ops.llm.langchain.core.prompt.base.template;

import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ChatPromptTemplate extends BaseChatPromptTemplate {

    private List<BaseVariable> inputVariables;

    private List<BaseMessagePromptTemplate> messagePromptTemplates;

    private ChatPromptTemplate(List<BaseMessagePromptTemplate> messagePromptTemplates) {
        this.messagePromptTemplates = messagePromptTemplates;
    }

    private ChatPromptTemplate(List<BaseVariable> inputVariables, List<BaseMessagePromptTemplate> messagePromptTemplates) {
        this.messagePromptTemplates = messagePromptTemplates;
        this.inputVariables = inputVariables;
    }

    public static ChatPromptTemplate fromMessages(List<BaseMessagePromptTemplate> promptTemplates) {

        return new ChatPromptTemplate(promptTemplates);
    }


    @Override
    public String format(List<BaseVariable> variables) {

        return this.formatPrompt(variables).toStr();
    }

    @Override
    protected List<BaseMessage> formatMessage(List<BaseVariable> variables) {

        List<BaseMessage> messageList = new ArrayList<>();
        Optional.ofNullable(messagePromptTemplates).orElse(new ArrayList<>()).stream().forEach((promptTemplate) -> {
            messageList.addAll(promptTemplate.formatMessages(variables));
        });

        return messageList;
    }


    public static ChatPromptTemplate fromTemplate() {
        return null;
    }


    public static ChatPromptTemplate fromRoleStrings() {
        return null;
    }

    public static ChatPromptTemplate fromStrings() {
        return null;
    }

    public static ChatPromptTemplate fromMessages() {
        return null;
    }

}
