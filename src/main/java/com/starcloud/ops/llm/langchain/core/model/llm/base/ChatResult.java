package com.starcloud.ops.llm.langchain.core.model.llm.base;

import cn.hutool.core.util.StrUtil;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author df007df
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class ChatResult<R> {

    private List<ChatGeneration<R>> chatGenerations;

    private Object llmOutput;

    private BaseLLMUsage usage;

    private String text;

    public String getText() {
        return StrUtil.isNotBlank(this.text) ? this.text : Optional.ofNullable(chatGenerations).orElse(new ArrayList<>()).stream().findFirst().map(ChatGeneration::getText).orElse("");
    }

    public static <R> ChatResult<R> data(String str) {
        return ChatResult.<R>builder().chatGenerations(null).build();
    }

    public static <R> ChatResult<R> data(List<ChatGeneration<R>> generations, Object output) {
        return ChatResult.<R>builder().chatGenerations(generations).llmOutput(output).build();
    }

    public static <R> ChatResult<R> data(List<ChatGeneration<R>> generations, Object output, BaseLLMUsage usage) {
        return ChatResult.<R>builder().chatGenerations(generations).llmOutput(output).usage(usage).build();
    }

    public static <R> ChatResult<R> data(List<ChatGeneration<R>> generations, BaseLLMUsage usage) {
        return ChatResult.<R>builder().chatGenerations(generations).usage(usage).build();
    }
}
