package com.starcloud.ops.llm.langchain.core.tools.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kjetland.jackson.jsonSchema.JsonSchemaConfig;
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator;
import lombok.Data;

import java.io.IOException;
import java.util.Map;

@Data
public class OpenAIUtils {

    private static ObjectMapper mapper;
    private static JsonSchemaConfig config;
    private static JsonSchemaGenerator jsonSchemaGenerator;

    static {
        mapper = new ObjectMapper();
        config = JsonSchemaConfig.vanillaJsonSchemaDraft4();
        jsonSchemaGenerator = new JsonSchemaGenerator(mapper, config);
    }

    public static JsonNode serializeJsonSchema(Class<?> cls) {

        try {

            JsonNode schema = jsonSchemaGenerator.generateJsonSchema(cls);
            return schema;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate JSON Schema", e);
        }

    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> writeAndMap(JsonNode value)
            throws IOException {

        String str = mapper.writeValueAsString(value);
        return (Map<String, Object>) mapper.readValue(str, Map.class);
    }

    public static JsonNode valueToTree(Map<String, Object> jsonSchema) {

        JsonNode jsonNode = mapper.valueToTree(jsonSchema);
        return jsonNode;
    }

    public static JsonNode valueToTree(Object jsonSchema) {

        JsonNode jsonNode = mapper.valueToTree(jsonSchema);
        return jsonNode;
    }

}

