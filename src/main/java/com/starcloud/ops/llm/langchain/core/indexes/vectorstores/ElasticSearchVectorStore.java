package com.starcloud.ops.llm.langchain.core.indexes.vectorstores;

import cn.hutool.core.collection.CollectionUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.KnnQuery;
import co.elastic.clients.elasticsearch._types.Script;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQuery;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.DeleteByQueryRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.starcloud.ops.llm.langchain.core.model.llm.document.DocumentSegmentDTO;
import com.starcloud.ops.llm.langchain.core.model.llm.document.KnnQueryDTO;
import com.starcloud.ops.llm.langchain.core.model.llm.document.KnnQueryHit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = "starcloud-llm.vector.store", havingValue = "elasticsearch")
@Slf4j
public class ElasticSearchVectorStore implements BasicVectorStore {

    @Value("${starcloud.elasticsearch.index.name:vector_index_l2}")
    private String indexName;

    @Autowired
    private ElasticsearchClient esClient;

    @Override
    public void addSegment(List<DocumentSegmentDTO> segments) {
        List<BulkOperation> operations = new ArrayList<>();
        for (DocumentSegmentDTO document : segments) {
            BulkOperation operation = BulkOperation.of(builder -> builder
                    .index(index -> index
                            .index(indexName)
                            .document(document)
                            .id(document.getSegmentId())
                    ));
            operations.add(operation);
        }
        BulkRequest request = BulkRequest.of(builder -> builder
                .operations(operations));
        try {
            esClient.bulk(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<KnnQueryHit> knnSearch(List<Float> queryVector, KnnQueryDTO queryDTO) {
        queryDTO.checkDefaultValue();
        TermQuery status = TermQuery.of(t -> t.field("status").value(true));

        TermsQuery termsQuery;
        if (CollectionUtil.isNotEmpty(queryDTO.getDatasetIds())) {
            List<FieldValue> datasetIds = queryDTO.getDatasetIds().stream().map(FieldValue::of).collect(Collectors.toList());
            termsQuery = TermsQuery.of(t -> t.field("datasetId").terms(f -> f.value(datasetIds)));
        } else if (CollectionUtil.isNotEmpty(queryDTO.getDocumentIds())) {
            List<FieldValue> documentIds = queryDTO.getDocumentIds().stream().map(FieldValue::of).collect(Collectors.toList());
            termsQuery = TermsQuery.of(t -> t.field("documentId").terms(f -> f.value(documentIds)));
        } else if (CollectionUtil.isNotEmpty(queryDTO.getSegmentIds())) {
            List<FieldValue> segmentIds = queryDTO.getSegmentIds().stream().map(FieldValue::of).collect(Collectors.toList());
            termsQuery = TermsQuery.of(t -> t.field("segmentId").terms(f -> f.value(segmentIds)));
        } else {
            throw new IllegalArgumentException("数据集id、文档id、分段id不能同时为空");
        }
        BoolQuery boolQuery = BoolQuery.of(b -> b.must(new Query(status)).must(new Query(termsQuery)));
        Query query = Query.of(q -> q.bool(boolQuery));

        KnnQuery knnQuery = new KnnQuery.Builder()
                .k(queryDTO.getK())
                .field("vector")
                .numCandidates(queryDTO.getNumCandidates())
                .queryVector(queryVector)
                .filter(query)
                .build();
        SearchRequest vector = new SearchRequest.Builder()
                .minScore(queryDTO.getMinScore() == null ? 0 : queryDTO.getMinScore())
                .knn(knnQuery).index(indexName).build();
        try {
            SearchResponse<DocumentSegmentDTO> search = esClient.search(vector, DocumentSegmentDTO.class);
            hit(search.hits().hits());
            return search.hits().hits().stream().map(hit -> {
                return KnnQueryHit.builder().score(hit.score()).document(hit.source()).build();
            }).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteSegment(List<String> documentIds) {
        List<FieldValue> documentField = documentIds.stream().map(FieldValue::of).collect(Collectors.toList());
        TermsQuery terms = TermsQuery.of(t -> t.field("documentId").terms(f -> f.value(documentField)));
        Query query = Query.of(q -> q.bool(b -> b.must(new Query(terms))));
        DeleteByQueryRequest delete = DeleteByQueryRequest.of(d -> d.index(indexName).query(query));
        try {
            esClient.deleteByQuery(delete);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateSegment(List<DocumentSegmentDTO> documentDTOS) {
        List<BulkOperation> operations = new ArrayList<>();
        for (DocumentSegmentDTO documentSegment : documentDTOS) {
            BulkOperation operation = BulkOperation.of(builder -> builder
                    .update(index -> index
                            .id(documentSegment.getSegmentId())
                            .action(a -> a.doc(documentSegment))
                    ));
            operations.add(operation);
        }
        BulkRequest request = BulkRequest.of(builder -> builder
                .index(indexName)
                .operations(operations));
        try {
            esClient.bulk(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void hit(List<Hit<DocumentSegmentDTO>> hits) {
        if (CollectionUtil.isEmpty(hits)) {
            return;
        }
        List<String> segmentIds = hits.stream()
                .filter(dtoHit -> dtoHit.source() != null)
                .map(dtoHit -> {
                    DocumentSegmentDTO segmentDTO = dtoHit.source();
                    segmentDTO.setHitCount(segmentDTO.getHitCount() == null ? 1 : segmentDTO.getHitCount() + 1);
                    return segmentDTO.getSegmentId();
                }).collect(Collectors.toList());

        if (CollectionUtil.isEmpty(segmentIds)) {
            return;
        }
        List<FieldValue> segmentField = segmentIds.stream().map(FieldValue::of).collect(Collectors.toList());
        TermsQuery terms = TermsQuery.of(t -> t.field("segmentId").terms(f -> f.value(segmentField)));
        Query query = Query.of(q -> q.bool(b -> b.must(new Query(terms))));
        Script of = Script.of(s -> s.inline(i -> i.source("if(ctx._source.hitCount == null) { ctx._source.hitCount = 1} else { ctx._source.hitCount += 1}")));
        try {
            esClient.updateByQuery(q -> q.index(indexName).query(query).script(of));
        } catch (IOException e) {
            log.info("hit count error", e);
        }

    }
}
