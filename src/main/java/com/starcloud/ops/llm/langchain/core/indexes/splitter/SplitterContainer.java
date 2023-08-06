package com.starcloud.ops.llm.langchain.core.indexes.splitter;

public enum SplitterContainer {

    CHARACTER_TEXT_SPLITTER(new CharacterTextSplitter()),

    TOKEN_TEXT_SPLITTER(new TokenTextSplitter());

    private BasicTextSplitter splitter;

    SplitterContainer(BasicTextSplitter splitter) {
        this.splitter = splitter;
    }

    public BasicTextSplitter getSplitter() {
        return splitter;
    }
}
