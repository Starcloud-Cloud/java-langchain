package com.starcloud.ops.llm.langchain.core.utils;


import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.*;
import com.starcloud.ops.llm.langchain.core.schema.message.multimodal.MultiModalMessage;
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

            if (baseMessage.getAdditionalArgs().get("function_call") instanceof ChatFunctionCall) {
                //  ChatFunctionCall call = JsonUtils.parseObject(JsonUtils.toJsonString(baseMessage.getAdditionalArgs().get("function_call")), ChatFunctionCall.class);
                chatMessage.setFunctionCall((ChatFunctionCall) baseMessage.getAdditionalArgs().get("function_call"));
            }

            return chatMessage;

        } else if (baseMessage instanceof HumanMessage) {

            return new ChatMessage("user", content);

        } else if (baseMessage instanceof FunctionMessage) {

            return new ChatMessage("function", content, ((FunctionMessage) baseMessage).getName());
        } else {

            return new ChatMessage(baseMessage.getType(), content);

        }

    }


    /**
     * 千问
     *
     * @param baseMessage
     * @return
     */
    public static Message BaseMessage2QwenMessage(BaseMessage baseMessage) {

        String role = baseMessage.getType();
        String content = baseMessage.getContent();
        Map<String, Object> args = baseMessage.getAdditionalArgs();


        if (baseMessage instanceof SystemMessage) {

            return Message.builder().role(Role.SYSTEM.getValue()).content(content).build();

        } else if (baseMessage instanceof AIMessage) {

            return Message.builder().role(Role.ASSISTANT.getValue()).content(content).build();

        } else if (baseMessage instanceof HumanMessage) {

            return Message.builder().role(Role.USER.getValue()).content(content).build();

        } else {

            return Message.builder().role(baseMessage.getType()).content(content).build();
        }

    }


    /**
     * 千问
     *
     * @param baseMessage
     * @return
     */
    public static com.alibaba.dashscope.common.MultiModalMessage BaseMessage2QwenMessage(MultiModalMessage baseMessage) {

        String role = baseMessage.getType();
        String content = baseMessage.getContent();
        List<Map<String, Object>> contents = baseMessage.getContents();

        if (baseMessage instanceof com.starcloud.ops.llm.langchain.core.schema.message.multimodal.SystemMessage) {

            return com.alibaba.dashscope.common.MultiModalMessage.builder().role(Role.SYSTEM.getValue()).content(contents).build();

        } else if (baseMessage instanceof com.starcloud.ops.llm.langchain.core.schema.message.multimodal.AIMessage) {

            return com.alibaba.dashscope.common.MultiModalMessage.builder().role(Role.ASSISTANT.getValue()).content(contents).build();

        } else if (baseMessage instanceof com.starcloud.ops.llm.langchain.core.schema.message.multimodal.HumanMessage) {

            return com.alibaba.dashscope.common.MultiModalMessage.builder().role(Role.USER.getValue()).content(contents).build();

        } else {

            return com.alibaba.dashscope.common.MultiModalMessage.builder().role(baseMessage.getType()).content(contents).build();
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
                AIMessage aiMessage = new AIMessage(chatMessage.getContent());
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
