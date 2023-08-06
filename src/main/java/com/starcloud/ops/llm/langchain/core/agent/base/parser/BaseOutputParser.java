package com.starcloud.ops.llm.langchain.core.agent.base.parser;

import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseGeneration;
import com.starcloud.ops.llm.langchain.core.prompt.base.PromptValue;

import java.util.List;

public abstract class BaseOutputParser<T> implements BaseLLMOutputParser<T> {

    @Override
    public T parseResult(List<BaseGeneration> generations) {

        return this.parse(generations.get(0).getText());
    }

    public abstract T parse(String text);

    public Object parseWithPrompt(String completion, PromptValue promptValue) {

        return null;
    }


}
