package com.starcloud.ops.llm.langchain.core.tools.base;

import co.elastic.clients.elasticsearch.xpack.usage.Base;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class ExceptionTool extends BaseTool<String> {

    private String name = "_Exception";

    private String description = "Exception tool";


    @Override
    protected ToolResponse _run(String input) {
        return ToolResponse.buildObservation(input.toString());
    }


    @Override
    public JsonNode getInputSchemas() {
        return null;
    }


}
