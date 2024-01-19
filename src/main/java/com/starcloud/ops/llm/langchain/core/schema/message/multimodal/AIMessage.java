package com.starcloud.ops.llm.langchain.core.schema.message.multimodal;

import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;

import java.util.List;
import java.util.Map;

public class AIMessage extends MultiModalMessage {

    public AIMessage(List<Map<String, Object>> content) {
        super(content);
    }

    //@todo
    @Override
    public String getType() {

        return "assistant";
    }


}
