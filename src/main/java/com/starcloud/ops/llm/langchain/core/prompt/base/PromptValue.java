package com.starcloud.ops.llm.langchain.core.prompt.base;

import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;

import java.util.List;

public abstract class PromptValue {

    public abstract String toStr();

    public abstract List<? extends BaseMessage> toMessage();

}
