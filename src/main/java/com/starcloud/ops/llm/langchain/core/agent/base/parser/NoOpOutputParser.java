package com.starcloud.ops.llm.langchain.core.agent.base.parser;

public class NoOpOutputParser extends BaseOutputParser<String> {

    @Override
    public String parse(String text) {
        return null;
    }
}
