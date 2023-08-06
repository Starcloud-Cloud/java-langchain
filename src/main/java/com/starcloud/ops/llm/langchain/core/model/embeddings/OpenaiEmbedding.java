package com.starcloud.ops.llm.langchain.core.model.embeddings;

import com.starcloud.ops.llm.langchain.core.model.llm.document.EmbeddingDetail;
import com.theokanning.openai.embedding.Embedding;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.embedding.EmbeddingResult;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(value = "starcloud.llm.embedding.model", havingValue = "opeanai", matchIfMissing = true)
public class OpenaiEmbedding implements BasicEmbedding {

    private static final String MODEL = "text-embedding-ada-002";

    @Value("${starcloud-langchain.model.llm.openai.apiKey:}")
    private String chatAiKey;

    @Override
    public List<List<Float>> embedTexts(List<String> texts) {
        OpenAiService service = new OpenAiService(chatAiKey, Duration.ofSeconds(60L));
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
        OpenAiService service = new OpenAiService(chatAiKey, Duration.ofSeconds(60L));
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

