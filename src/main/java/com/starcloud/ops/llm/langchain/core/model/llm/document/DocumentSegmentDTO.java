package com.starcloud.ops.llm.langchain.core.model.llm.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentSegmentDTO {

    /**
     * 数据集 id
     */
    private String dataSetId;

    /**
     * 文档 id
     */
    private String documentId;

    /**
     * segment id
     */
    private String segmentId;

    List<Float> vector;

    private String segmentText;

    private Long tenantId;

    private String creator;

    private Boolean status;

}
