package com.starcloud.ops.llm.langchain.core.schema.message;

import cn.hutool.core.util.StrUtil;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


@NoArgsConstructor
@Data
public abstract class BaseMessage implements Serializable {

    private String content;

    private Map<String, Object> additionalArgs = new HashMap<>();

    public abstract String getType();

    public BaseMessage(String content) {
        this.content = content;
    }


    public BaseMessage(String content, Map<String, Object> additionalArgs) {
        this.content = content;
        this.additionalArgs = additionalArgs;
    }

    public static String getBufferString(List<BaseMessage> messages) {
        return Optional.ofNullable(messages).orElse(new ArrayList<>()).stream().map(message -> {

            String role = message.getType();
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

            String content = role + ": " + message.getContent();

            if (message instanceof AIMessage) {
                String call = (String) message.getAdditionalArgs().get("function_call");
                if (StrUtil.isNotBlank(call)) {
                    content += "{" + call + "}";
                }
            }

            return content;

        }).collect(Collectors.joining("\n"));
    }

}
