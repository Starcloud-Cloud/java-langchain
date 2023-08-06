package com.starcloud.ops.llm.langchain.core.model.chat.base.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SuperBuilder
@AllArgsConstructor
@Data
public class BaseChatMessage implements Serializable {

    private String role;

    private String content;

    private Long tokens;

    public BaseChatMessage() {
    }

    public BaseChatMessage(String content) {
        this.content = content;
    }


    @Deprecated
    public static BaseChatMessage ofRole(String role) {

        switch (role) {
            case "assistant":
            case "ai":
                return AIMessage.builder().build();
            case "user":
            case "human":
                return HumanMessage.builder().build();
            case "function":
                return FunctionMessage.builder().build();
            default:
                return SystemMessage.builder().build();
        }
    }


    @Deprecated
    public static String getBufferString(List<BaseChatMessage> messages) {
        return Optional.ofNullable(messages).orElse(new ArrayList<>()).stream().map(message -> {

            String role = message.getRole();
            if (message instanceof HumanMessage) {
                role = "Human";
            } else if (message instanceof AIMessage) {
                role = "AI";
            } else if (message instanceof SystemMessage) {
                role = "System";
            } else if (message instanceof FunctionMessage) {
                role = "Function";
            } else {
                role = "Human";
            }
            return role + ": " + message.getContent();
        }).collect(Collectors.joining("\n"));
    }
}
