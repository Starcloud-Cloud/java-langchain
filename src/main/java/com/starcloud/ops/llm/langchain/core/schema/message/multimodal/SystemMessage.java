package com.starcloud.ops.llm.langchain.core.schema.message.multimodal;

import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;

import java.util.List;
import java.util.Map;

public class SystemMessage extends MultiModalMessage {

    public SystemMessage(List<Map<String, Object>> content) {
        super(content);
    }

    @Override
    public String getType() {

        return "system";
    }
}
