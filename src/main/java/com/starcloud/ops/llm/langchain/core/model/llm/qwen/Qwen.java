package com.starcloud.ops.llm.langchain.core.model.llm.qwen;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.models.QwenParam;
import com.starcloud.ops.llm.langchain.config.QwenAIConfig;
import com.starcloud.ops.llm.langchain.core.callbacks.CallbackManagerForLLMRun;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseGeneration;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLM;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMUsage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 通义千问
 *
 * @author df007df
 */
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Qwen extends BaseLLM<GenerationResult> {

    private static QwenAIConfig qwenAIConfig = SpringUtil.getBean("qwenAIConfig");

    private String model = Generation.Models.QWEN_V1;

    private String prompt;

    private Double topP = 0.5d;

    private Integer topK = 0;

    private Boolean stream = false;

    private Boolean enableSearch = false;

    private int seed;

    private String resultFormat = "message";


    @Override
    public String getModelType() {
        return this.getModel();
    }

    @Override
    protected BaseLLMResult<GenerationResult> _generate(List<String> texts, CallbackManagerForLLMRun callbackManager) {

        if (CollectionUtil.size(texts) > 1) {
            throw new RuntimeException("Cannot stream results with multiple prompts.");
        }

        try {

            String modelName = this.getModel();

            QwenParam qwenParam = QwenParam.builder().model(this.getModel()).enableSearch(this.getEnableSearch()).resultFormat(this.getResultFormat()).build();

            //上游最大值是2，这里最大值是1
            Double top = Double.valueOf(NumberUtil.decimalFormat("0.0", this.getTopP() / 2));
            qwenParam.setTopP(top);

            qwenParam.setApiKey(qwenAIConfig.getApiKey());
            qwenParam.setPrompt(texts.get(0));

            Generation gen = new Generation();
            GenerationResult result = gen.call(qwenParam);
            String text = result.getOutput().getChoices().get(0).getMessage().getContent();

            BaseLLMUsage usage = BaseLLMUsage.builder()
                    .promptTokens(Long.valueOf(result.getUsage().getInputTokens()))
                    .completionTokens(Long.valueOf(result.getUsage().getOutputTokens()))
                    .build();

            usage.setTotalTokens(usage.getPromptTokens() + usage.getCompletionTokens());

            return BaseLLMResult.data(Arrays.asList(BaseGeneration.<GenerationResult>builder().text(text).generationInfo(result).build()), new HashMap() {{
                put("model_name", modelName);
            }}, usage);

        } catch (Exception e) {

            log.error("Qwen _generate is fail: {}", e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);

        }
    }

}
