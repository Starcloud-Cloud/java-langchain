package com.starcloud.ops.llm.langchain.core.indexes.vectorstores;

import cn.hutool.core.collection.CollectionUtil;
import com.starcloud.ops.llm.langchain.core.model.llm.document.DocumentSegmentDTO;
import com.starcloud.ops.llm.langchain.core.model.llm.document.KnnQueryDTO;
import com.starcloud.ops.llm.langchain.core.model.llm.document.KnnQueryHit;
import org.apache.commons.math3.util.MathArrays;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * embedding保存在数据库中，在本地内存中做l2向量距离计算
 */
@Component
@ConditionalOnProperty(name = "starcloud-llm.vector.store", havingValue = "default")
public class DefaultVectorStore implements BasicVectorStore {

    /**
     * 向量放在数据库中
     * <p>
     * create table embeddings if not exists
     * (
     * id         uuid,
     * hash       varchar(64),   判断重复数据 不再重新计算embeddings
     * embedding  BLOB,
     * created_at timestamp
     * );
     */

    @Resource(name = "defaultRepository")
    private SegmentEmbeddingMapper mapper;


    @Override
    public void addSegment(List<DocumentSegmentDTO> segments) {
        //todo 不需插入 复用llm_segments_embeddings中的向量
    }

    @Override
    public List<KnnQueryHit> knnSearch(List<Float> queryVector, KnnQueryDTO queryDTO) {
        queryDTO.checkDefaultValue();
        List<Map<String, Object>> maps = null;
        if (CollectionUtil.isNotEmpty(queryDTO.getDatasetIds())) {
            maps = mapper.selectByDataSetIds(queryDTO.getDatasetIds());
        } else if (CollectionUtil.isNotEmpty(queryDTO.getDocumentIds())) {
            maps = mapper.selectByDocIds(queryDTO.getDocumentIds());
        } else if (CollectionUtil.isNotEmpty(queryDTO.getSegmentIds())) {
            maps = mapper.selectByDocIds(queryDTO.getSegmentIds());
        } else {
            throw new IllegalArgumentException("数据集id、文档id、分段id不能同时为空");
        }
        List<KnnQueryHit> knnQueryHitList = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            List<Float> vector = deserialize((byte[]) map.get("vector"));
            DocumentSegmentDTO documentSegment = DocumentSegmentDTO.builder()
                    .tenantId(Long.valueOf(map.get("tenant_id").toString()))
                    .datasetId(String.valueOf(map.get("dataset_id")))
                    .documentId(String.valueOf(map.get("document_id")))
                    .segmentId(String.valueOf(map.get("segment_id")))
                    .content(String.valueOf(map.get("content")))
                    .vector(vector)
                    .build();
            double[] docVec = vector.stream().mapToDouble(Float::floatValue).toArray();
            double[] queryVec = queryVector.stream().mapToDouble(Float::floatValue).toArray();
            double score = 1 - MathArrays.distance(docVec, queryVec);
            KnnQueryHit knnQueryHit = KnnQueryHit.builder().document(documentSegment).score(score).build();
            knnQueryHitList.add(knnQueryHit);

        }
        // desc sort
        knnQueryHitList.sort((a, b) -> a.getScore() > b.getScore() ? -1 : 1);
        return knnQueryHitList.subList(0, (int) Math.min(queryDTO.getK(),knnQueryHitList.size()));
    }

    private List<Float> deserialize(byte[] data) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bis)) {

            return (List<Float>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteSegment(List<String> documentIds) {
        mapper.deleteByDocIds(documentIds);
    }

    @Override
    public void updateSegment(List<DocumentSegmentDTO> documentDTOS) {

    }


    public interface SegmentEmbeddingMapper {

        @Select("<script> "
                + "select e.tenant_id,e.dataset_id,e.document_id,e.segment_id,e.vector,s.content"
                + " from llm_document_segments s INNER JOIN llm_segments_embeddings e ON s.id =  e.segment_id"
                + " where s.deleted = false and s.id in "
                + "  <foreach collection='list' item='item' index='index' "
                + "    open='(' separator=',' close=')' >                 "
                + "    #{item}                                            "
                + "  </foreach>"
                + "</script>"
        )
        List<Map<String, Object>> selectBySegmentIds(List<String> list);


        @Select("<script> "
                + "select e.tenant_id,e.dataset_id,e.document_id,e.segment_id,e.vector,s.content"
                + " from llm_document_segments s INNER JOIN llm_segments_embeddings e ON s.id =  e.segment_id"
                + " where s.deleted = false and s.document_id in "
                + "  <foreach collection='list' item='item' index='index' "
                + "    open='(' separator=',' close=')' >                 "
                + "    #{item}                                            "
                + "  </foreach>"
                + "</script>"
        )
        List<Map<String, Object>> selectByDocIds(List<String> list);

        @Select("<script> "
                + "select e.tenant_id,e.dataset_id,e.document_id,e.segment_id,e.vector,s.content"
                + " from llm_document_segments s INNER JOIN llm_segments_embeddings e ON s.id =  e.segment_id"
                + " where s.deleted = false and s.dataset_id in "
                + "  <foreach collection='list' item='item' index='index' "
                + "    open='(' separator=',' close=')' >                 "
                + "    #{item}                                            "
                + "  </foreach>"
                + "</script>"
        )
        List<Map<String, Object>> selectByDataSetIds(List<String> list);

        @Update(
                "<script> "
                + "update llm_segments_embeddings set deleted = true where document_id in "
                + "  <foreach collection='list' item='item' index='index' "
                + "    open='(' separator=',' close=')' >                 "
                + "    #{item}                                            "
                + "  </foreach>"
                + "</script>"
        )
        int deleteByDocIds(List<String> list);

    }


}
