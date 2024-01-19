package com.starcloud.ops.llm.langchain.core.schema.message;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import com.starcloud.ops.llm.langchain.core.utils.JsonUtils;
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
        if (!StrUtil.isEmpty(content)) {
            this.content = content;
        } else {
            this.content = "";
        }
    }

    public BaseMessage(String content, Map<String, Object> additionalArgs) {
        if (!StrUtil.isEmpty(content)) {
            this.content = content;
        } else {
            this.content = "";
        }
        this.additionalArgs = additionalArgs;
    }

    public static String getBufferString(List<BaseMessage> messages) {
        return Optional.ofNullable(messages).orElse(new ArrayList<>()).stream().map(BaseMessage::getBufferString).collect(Collectors.joining("\n")) + "\n";
    }

    public static String getBufferString(BaseMessage message) {
        String role = message.getType();
        String content = message.getContent();

        if (message instanceof HumanMessage) {
            role = "Human";
        } else if (message instanceof AIMessage) {
            role = "AI";
            Object call = message.getAdditionalArgs().get("function_call");
            if (ObjectUtil.isNotNull(call)) {
                //这时候 message.getContent() 其实为空
                Map _hasMap = new HashMap() {{
                    put("function_call", call);
                }};
                content += JsonUtils.toJsonString(_hasMap);
            }
        } else if (message instanceof SystemMessage) {
            role = "System";
        } else if (message instanceof FunctionMessage) {
            role = "Function";
            String finalContent = content;
            Map _hasMap = new HashMap() {{
                put("name", ((FunctionMessage) message).getName());
                put("content", finalContent);
            }};
            content = JsonUtils.toJsonString(_hasMap);
        } else {
            role = "Human";
        }

        content = role + ": " + content;

        return content;
    }

}
