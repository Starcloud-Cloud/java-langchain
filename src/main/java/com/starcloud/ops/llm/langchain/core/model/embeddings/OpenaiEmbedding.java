package com.starcloud.ops.llm.langchain.core.model.embeddings;

import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starcloud.ops.llm.langchain.config.OpenAIConfig;
import com.starcloud.ops.llm.langchain.core.model.llm.document.EmbeddingDetail;
import com.theokanning.openai.OpenAiApi;
import com.theokanning.openai.embedding.Embedding;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.embedding.EmbeddingResult;
import com.theokanning.openai.service.OpenAiService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import retrofit2.Retrofit;

import java.io.IOException;
import java.net.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.theokanning.openai.service.OpenAiService.*;

@Slf4j
@Component
@ConditionalOnProperty(value = "starcloud.llm.embedding.model", havingValue = "opeanai", matchIfMissing = true)
public class OpenaiEmbedding implements BasicEmbedding {

    private static final String MODEL = "text-embedding-ada-002";

    @Autowired
    private OpenAIConfig openAIConfig;

    @Override
    public List<List<Float>> embedTexts(List<String> texts) {
        OpenAiService service = new OpenAiService(openAIConfig.getApiKey(), Duration.ofSeconds(60L));
        EmbeddingRequest request = EmbeddingRequest.builder()
                .input(texts)
                .model(MODEL).build();
        EmbeddingResult embeddings = service.createEmbeddings(request);
        List<List<Float>> result = new ArrayList<>();
        for (Embedding datum : embeddings.getData()) {
            List<Float> floats = datum.getEmbedding()
                    .stream().map(Double::floatValue).collect(Collectors.toList());
            result.add(floats);
        }
        return result;
    }

    @Override
    public EmbeddingDetail embedText(String text) {
        OpenAiService service = new OpenAiService(openAIConfig.getApiKey(), Duration.ofSeconds(60L));
        EmbeddingRequest request = EmbeddingRequest.builder()
                .input(Arrays.asList(text))
                .model(MODEL).build();
        EmbeddingResult embeddingResult = service.createEmbeddings(request);

        return EmbeddingDetail.builder().embedding(embeddingResult.getData().get(0).getEmbedding().stream().map(Double::floatValue).collect(Collectors.toList()))
                .promptTokens(embeddingResult.getUsage().getPromptTokens())
                .completionTokens(embeddingResult.getUsage().getCompletionTokens())
                .totalTokens(embeddingResult.getUsage().getTotalTokens()).build();
    }

}

