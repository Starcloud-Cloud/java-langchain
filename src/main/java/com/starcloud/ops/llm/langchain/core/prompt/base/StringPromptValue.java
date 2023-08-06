package com.starcloud.ops.llm.langchain.core.prompt.base;

import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.HumanMessage;
import lombok.Data;

import java.util.Arrays;
import java.util.List;


@Data
public class StringPromptValue extends PromptValue {

    private String str;

    public StringPromptValue(String str) {
        this.str = str;
    }

    @Override
    public String toStr() {
        return this.str;
    }

    @Override
    public List<BaseMessage> toMessage() {
        return Arrays.asList(new HumanMessage(this.str));
    }
}
