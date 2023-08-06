package com.starcloud.ops.llm.langchain.core.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.llm.langchain.core.tools.base.BaseRequestsTool;
import com.starcloud.ops.llm.langchain.core.tools.base.BaseTool;
import lombok.Data;

@Data
public class InvalidTool extends BaseTool<Object, String> implements BaseRequestsTool {

    private String name = "invalid_tool";

    private String description = "Called when tool name is invalid.";

    @Override
    protected String _run(Object input) {
        return this.name + " is not a valid tool, try another one.";
    }


    @Override
    public JsonNode getInputSchemas() {
        return null;
    }
}
