package com.starcloud.ops.llm.langchain.core.utils;


import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.*;
import com.theokanning.openai.completion.chat.ChatFunctionCall;
import com.theokanning.openai.completion.chat.ChatMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MessageConvert {

    public static ChatMessage OpenAIMessage(BaseMessage baseMessage) {

        String type = baseMessage.getType();
        if (baseMessage instanceof HumanMessage) {
            type = "user";
        }
        return new ChatMessage(type, baseMessage.getContent());
    }

    public static ChatMessage BaseMessage2ChatMessage(BaseMessage baseMessage) {

        String role = baseMessage.getType();
        String content = baseMessage.getContent();
        Map<String, Object> args = baseMessage.getAdditionalArgs();

        if (baseMessage instanceof SystemMessage) {

            return new ChatMessage("system", content);

        } else if (baseMessage instanceof AIMessage) {

            ChatMessage chatMessage = new ChatMessage("assistant", content);

            ChatFunctionCall call = (ChatFunctionCall) baseMessage.getAdditionalArgs().get("function_call");
            chatMessage.setFunctionCall(call);

            return chatMessage;

        } else if (baseMessage instanceof HumanMessage) {

            return new ChatMessage("user", content);

        } else if (baseMessage instanceof FunctionMessage) {

            return new ChatMessage("function", content, ((FunctionMessage) baseMessage).getName());
        } else {

            return new ChatMessage(baseMessage.getType(), content);

        }

    }


    public static BaseMessage convertToMessage(ChatMessage chatMessage) {

        switch (chatMessage.getRole()) {

            case "user":
                return new HumanMessage(chatMessage.getContent());
            case "assistant":
//                if (chatMessage.getFunctionCall() != null) {
//                    return new FunctionMessage(chatMessage.getFunctionCall().getName(), chatMessage.getFunctionCall().getArguments());
//                }
                AIMessage aiMessage =  new AIMessage(chatMessage.getContent());
                aiMessage.getAdditionalArgs().put("function_call", chatMessage.getFunctionCall());
                return aiMessage;
            case "system":
                return new SystemMessage(chatMessage.getContent());
            case "function":
                return new FunctionMessage(chatMessage.getFunctionCall().getName(), chatMessage.getFunctionCall().getArguments());
            default:
                return new com.starcloud.ops.llm.langchain.core.schema.message.ChatMessage(chatMessage.getContent(), chatMessage.getRole());

        }
    }


    public static BaseMessage fixMessage(BaseChatMessage baseMessage) {

        String role = baseMessage.getRole();
        String content = baseMessage.getContent();
        switch (role) {
            case "assistant":
            case "ai":
                return new AIMessage(content);
            case "user":
            case "human":
                return new HumanMessage(content);
            case "function":
                throw new RuntimeException("nonsupport");
            default:
                return new SystemMessage(content);
        }
    }

    public static List<BaseMessage> fixMessageList(List<BaseChatMessage> baseChatMessages) {

        return Optional.ofNullable(baseChatMessages).orElse(new ArrayList<>()).stream().map(MessageConvert::fixMessage).collect(Collectors.toList());
    }

    public static BaseChatMessage fixMessage(BaseMessage baseMessage) {

        BaseChatMessage baseChatMessage = BaseChatMessage.ofRole(baseMessage.getType());
        baseChatMessage.setContent(baseMessage.getContent());
        return baseChatMessage;
    }

    public static List<BaseChatMessage> fixMessage(List<BaseMessage> baseMessages) {

        return Optional.ofNullable(baseMessages).orElse(new ArrayList<>()).stream().map(MessageConvert::fixMessage).collect(Collectors.toList());

    }


}
