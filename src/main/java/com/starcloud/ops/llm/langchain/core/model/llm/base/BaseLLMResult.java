package com.starcloud.ops.llm.langchain.core.model.llm.base;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author df007df
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BaseLLMResult<R> {

    private List<? extends BaseGeneration<R>> generations;

    private Map llmOutput;

    private BaseLLMUsage usage;

    private String text;


    public String getText() {
        return StrUtil.isNotBlank(this.text) ? this.text : generations.get(0).getText();
    }

    public static <R> BaseLLMResult<R> data(String str) {
        return BaseLLMResult.<R>builder().text(str).build();
    }

    public static <R> BaseLLMResult<R> data(List<? extends BaseGeneration<R>> generations, Map output) {
        return BaseLLMResult.<R>builder().generations(generations).llmOutput(output).build();
    }

    public static <R> BaseLLMResult<R> data(List<? extends BaseGeneration<R>> generations, Map output, BaseLLMUsage usage) {
        return BaseLLMResult.<R>builder().generations(generations).llmOutput(output).usage(usage).build();
    }

    public static <R> BaseLLMResult<R> data(List<? extends BaseGeneration<R>> generations, BaseLLMUsage usage) {
        return BaseLLMResult.<R>builder().generations(generations).usage(usage).build();
    }
}
