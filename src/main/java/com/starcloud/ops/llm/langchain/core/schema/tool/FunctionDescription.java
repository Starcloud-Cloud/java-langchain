package com.starcloud.ops.llm.langchain.core.schema.tool;

import cn.hutool.core.util.TypeUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.llm.langchain.core.tools.base.BaseTool;
import lombok.Data;

import java.lang.reflect.Type;

@Data
public class FunctionDescription {

    private String name;

    private String description;

    private Class<?> parameters;

    private JsonNode jsonSchema;

    @Deprecated
    public FunctionDescription(String name, String description, Class<?> qcls) {
        this.name = name;
        this.description = description;
        this.parameters = qcls;
    }

    public FunctionDescription(String name, String description) {
        this.name = name;
        this.description = description;

    }

    public FunctionDescription(String name, String description, JsonNode jsonSchema) {
        this.name = name;
        this.description = description;
        this.jsonSchema = jsonSchema;
    }

    public static FunctionDescription convert(BaseTool baseTool) {

        return new FunctionDescription(baseTool.getName(), baseTool.getDescription(), baseTool.getInputSchemas());
    }
}
