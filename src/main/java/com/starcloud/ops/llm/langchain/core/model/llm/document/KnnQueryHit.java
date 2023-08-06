package com.starcloud.ops.llm.langchain.core.model.llm.document;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KnnQueryHit {

    private DocumentSegmentDTO document;

    private Double score;
}
