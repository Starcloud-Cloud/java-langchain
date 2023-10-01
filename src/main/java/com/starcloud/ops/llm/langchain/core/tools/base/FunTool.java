package com.starcloud.ops.llm.langchain.core.tools.base;


import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.llm.langchain.core.tools.utils.OpenAIUtils;
import lombok.Data;

import java.util.function.Function;

/**
 * 回调类型工具
 *
 * @author df007df
 */
@Data
public class FunTool extends BaseTool<Object> {

    /**
     * 自定义的 schema
     */
    private JsonNode jsonSchema;

    private Function<Object, ToolResponse> function;

    private Class<?> inputCls;

    private Class<?> outputCls;


    /**
     * 直接传入json schema
     *
     * @param name
     * @param description
     * @param jsonSchema
     * @param function
     */
    public FunTool(String name, String description, JsonNode jsonSchema, Function<Object, ToolResponse> function) {
        this.setFunction(function);
        this.setName(name);
        this.setDescription(description);
        this.setJsonSchema(jsonSchema);
    }


    /**
     * 传入 Java class
     *
     * @param name
     * @param description
     * @param schemaCls
     * @param function
     */
    public FunTool(String name, String description, Class<?> schemaCls, Function<Object, ToolResponse> function) {

        this.setFunction(function);
        this.setName(name);
        this.setDescription(description);
        this.setInputCls(schemaCls);
        this.setJsonSchema(OpenAIUtils.serializeJsonSchema(schemaCls));

    }

    @Override
    protected ToolResponse _run(Object input) {

        //@todo 转换入参 为 jsonSchema
        Class<?> cc = this.getInputCls();
        input = JSONUtil.toBean(input.toString(), cc);

        return function.apply(input);
    }


    @Override
    public JsonNode getInputSchemas() {

        return this.jsonSchema;
    }


}
