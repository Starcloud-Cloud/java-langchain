package com.starcloud.ops.llm.langchain.core.tools.base;


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
public class FunTool extends BaseTool<Object, Object> {

    /**
     * 自定义的 schema
     */
    private JsonNode jsonSchema;

    private Function<Object, String> function;

    private Class<?> inputCls;

    private Class<?> outputCls;

//    public FunTool(String name, String description, JsonNode jsonSchema, Function<Object, String> function) {
//        this.setFunction(function);
//        this.setName(name);
//        this.setDescription(description);
//        this.setJsonSchema(jsonSchema);
//    }

    public FunTool(String name, String description, Class<?> schemaCls, Function<Object, String> function) {
        this.setFunction(function);
        this.setName(name);
        this.setDescription(description);
        this.setInputCls(schemaCls);
    }

    @Override
    protected String _run(Object input) {
        return function.apply(input);
    }


    @Override
    public JsonNode getInputSchemas() {

        return OpenAIUtils.serializeJsonSchema(this.getInputCls());
    }


}
