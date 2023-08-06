package com.starcloud.ops.llm.langchain.core.agent.base.parser;

import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseGeneration;

import java.util.List;

public interface BaseLLMOutputParser<T> {

    T parseResult(List<BaseGeneration> generations);

}
