package com.starcloud.ops.llm.langchain.core.model.embeddings;

import com.starcloud.ops.llm.langchain.core.model.llm.document.EmbeddingDetail;

import java.util.List;

public interface BasicEmbedding {


    List<List<Float>> embedTexts(List<String> texts);

    EmbeddingDetail embedText(String text);

}
