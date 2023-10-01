package com.starcloud.ops.llm.langchain.core.memory.template;

import com.starcloud.ops.llm.langchain.core.memory.BaseChatMemory;
import com.starcloud.ops.llm.langchain.core.prompt.base.HumanMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.SystemMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.BaseChatPromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.BaseMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.ChatPromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * 工具记忆动态生成prompt
 */
public class ChatMemoryPromptTemplate extends ChatPromptTemplate {


    private SystemMessagePromptTemplate systemMessagePromptTemplate;

    private HumanMessagePromptTemplate humanMessagePromptTemplate;

    private BaseChatMemory chatMemory;

    private Boolean hasAddInput = false;

    protected ChatMemoryPromptTemplate(SystemMessagePromptTemplate systemMessagePromptTemplate, HumanMessagePromptTemplate humanMessagePromptTemplate, BaseChatMemory chatMemory) {
        super(new ArrayList<>());
        this.systemMessagePromptTemplate = systemMessagePromptTemplate;
        this.humanMessagePromptTemplate = humanMessagePromptTemplate;
        this.chatMemory = chatMemory;
    }

    public static ChatMemoryPromptTemplate fromMessages(SystemMessagePromptTemplate systemMessagePromptTemplate, HumanMessagePromptTemplate humanMessagePromptTemplate, BaseChatMemory chatMemory) {

        return new ChatMemoryPromptTemplate(systemMessagePromptTemplate, humanMessagePromptTemplate, chatMemory);
    }


    @Override
    protected List<BaseMessage> formatMessage(List<BaseVariable> variables) {

        List<BaseMessage> messageList = new ArrayList<>();

        messageList.addAll(this.systemMessagePromptTemplate.formatMessages(variables));


        //加载记忆
        List<BaseMessage> historyMessage = this.chatMemory.getChatHistory().getMessages();

        messageList.addAll(historyMessage);

        //第一次调用时增加
        if (!this.hasAddInput) {
            messageList.addAll(this.humanMessagePromptTemplate.formatMessages(variables));
            this.hasAddInput = true;
        }

        return messageList;
    }


}
