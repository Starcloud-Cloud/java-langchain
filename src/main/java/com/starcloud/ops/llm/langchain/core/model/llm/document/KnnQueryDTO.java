package com.starcloud.ops.llm.langchain.core.model.llm.document;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class KnnQueryDTO {

    private List<String> segmentIds;

    private Long k = 2L;

    private Long numCandidates = 5L;

}
