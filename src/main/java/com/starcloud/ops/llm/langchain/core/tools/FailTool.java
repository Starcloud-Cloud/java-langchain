package com.starcloud.ops.llm.langchain.core.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.llm.langchain.core.tools.base.BaseRequestsTool;
import com.starcloud.ops.llm.langchain.core.tools.base.BaseTool;
import com.starcloud.ops.llm.langchain.core.tools.base.ToolResponse;
import lombok.Data;

@Data
public class FailTool extends BaseTool<Object> {

    private String name = "{fail_tool}";

    private String description = "Calling the tool failed and returned nothing.";

    public FailTool(String name) {
        this.name = name;
    }

    @Override
    protected ToolResponse _run(Object input) {
        return ToolResponse.buildObservation(this.name + " call failed with no return.");
    }


    @Override
    public JsonNode getInputSchemas() {
        return null;
    }
}
