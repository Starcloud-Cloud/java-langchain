package com.starcloud.ops.llm.langchain.core.model.llm.base;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.starcloud.ops.llm.langchain.config.OpenAIConfig;
import com.starcloud.ops.llm.langchain.core.callbacks.BaseCallbackManager;
import com.starcloud.ops.llm.langchain.core.callbacks.CallbackManagerForLLMRun;
import com.starcloud.ops.llm.langchain.core.schema.ModelTypeEnum;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import com.starcloud.ops.llm.langchain.core.schema.tool.FunctionDescription;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.service.OpenAiService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author df007df
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseOpenAI extends BaseLLM<CompletionResult> {

    private String model = ModelTypeEnum.GPT_3_5_TURBO.getName();

    private String prompt;

    private String suffix;

    private Integer maxTokens = 256;

    private Double temperature = 0.7;

    private Double topP = 1d;

    private Double presencePenalty = 0d;

    private Double frequencyPenalty = 0d;

    private Integer n = 1;

    private Integer bestOf = 1;

    private Boolean stream = false;

    private Integer logprobs;

    private Boolean echo;

    private List<String> stop;

    private Map<String, Integer> logitBias;

    private String user;


    private OpenAIConfig openAIConfig = SpringUtil.getBean("openAIConfig");


    @Override
    public String getModelType() {
        return this.getModel();
    }


    @Override
    protected BaseLLMResult<CompletionResult> _generate(List<String> texts, CallbackManagerForLLMRun callbackManager) {

        if (this.stream) {

            if (CollectionUtil.size(texts) > 1) {
                throw new RuntimeException("Cannot stream results with multiple prompts.");
            }

            return null;

        } else {

            OpenAiService openAiService = new OpenAiService(openAIConfig.getApiKey(), Duration.ofSeconds(openAIConfig.getTimeOut()));
            CompletionRequest completionRequest = BeanUtil.toBean(this, CompletionRequest.class);


            completionRequest.setPrompt(texts.get(0));

            CompletionResult completionResult = openAiService.createCompletion(completionRequest);
            String text = completionResult.getChoices().get(0).getText();


            //@todo need total
            BaseLLMUsage usage = BaseLLMUsage.builder()
                    .promptTokens(completionResult.getUsage().getPromptTokens())
                    .completionTokens(completionResult.getUsage().getCompletionTokens())
                    .totalTokens(completionResult.getUsage().getTotalTokens())
                    .build();

            return BaseLLMResult.data(Arrays.asList(BaseGeneration.<CompletionResult>builder().text(text).generationInfo(completionResult).build()), new HashMap() {{
                put("model_name", completionRequest.getModel());
            }}, usage);
        }

    }

}
