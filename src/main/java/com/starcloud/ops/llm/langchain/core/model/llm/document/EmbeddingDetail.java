package com.starcloud.ops.llm.langchain.core.model.llm.document;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EmbeddingDetail {

    List<Float> embedding;

    /**
     * The number of prompt tokens used.
     */
    long promptTokens;

    /**
     * The number of completion tokens used.
     */
    long completionTokens;

    /**
     * The number of total tokens used
     */
    long totalTokens;
}
