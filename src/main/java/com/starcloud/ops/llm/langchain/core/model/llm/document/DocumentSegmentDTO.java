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
    private String datasetId;

    /**
     * 文档 id
     */
    private String documentId;

    /**
     * segment id
     */
    private String segmentId;

    /**
     * 命中次数
     */
    private Integer hitCount;

    /**
     * 分段序号
     */
    private Integer position;

    private Integer wordCount;

    private Long tokens;

    private String segmentHash;

    List<Float> vector;

    private String content;

    private Long tenantId;

    private String creator;

    private Long createTime;

    private Boolean status;

}
