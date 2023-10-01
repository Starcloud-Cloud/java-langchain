package com.starcloud.ops.llm.langchain.core.indexes.vectorstores;

import com.starcloud.ops.llm.langchain.core.model.llm.document.DocumentSegmentDTO;
import com.starcloud.ops.llm.langchain.core.model.llm.document.KnnQueryDTO;
import com.starcloud.ops.llm.langchain.core.model.llm.document.KnnQueryHit;

import java.util.List;

public interface BasicVectorStore {

    /**
     * 新增分段
     * @param segments
     */
    void addSegment(List<DocumentSegmentDTO> segments);

    /**
     * 相似查询
     * @param queryVector
     * @param queryDTO
     * @return
     */
    List<KnnQueryHit> knnSearch(List<Float> queryVector, KnnQueryDTO queryDTO);

    /**
     * 删除分段
     * @param documentIds
     */
    void deleteSegment(List<String> documentIds);

    /**
     * 更新分段
     * @param documentDTOS
     */
    void updateSegment(List<DocumentSegmentDTO> documentDTOS);

}
