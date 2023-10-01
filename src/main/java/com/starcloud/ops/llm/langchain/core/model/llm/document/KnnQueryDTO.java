package com.starcloud.ops.llm.langchain.core.model.llm.document;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class KnnQueryDTO {


    private List<String> datasetIds;

    private List<String> documentIds;

    private List<String> segmentIds;


    private Long k;

    private Long numCandidates;

    private Double minScore;

    public void checkDefaultValue() {
        if (k == null || k <= 0L) {
            k = 2L;
        }
        if (numCandidates == null || numCandidates <= 0L) {
            numCandidates = 5L;
        }
    }
}
