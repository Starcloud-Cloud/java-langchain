package com.starcloud.ops.llm.langchain.core.tools.base;

import cn.hutool.core.util.TypeUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.llm.langchain.core.callbacks.BaseCallbackManager;
import com.starcloud.ops.llm.langchain.core.callbacks.CallbackManagerForToolRun;
import com.starcloud.ops.llm.langchain.core.tools.utils.OpenAIUtils;
import kotlin.jvm.Transient;
import lombok.Data;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Data
public abstract class BaseTool<Q, R> {

    private String name;

    private String description;

    private Boolean verbose;

    private Boolean returnDirect = false;

    @Transient
    private BaseCallbackManager callbackManager;

    protected abstract R _run(Q input);

    public R run(Q input) {
        return this.run(input, false, new HashMap<>());
    }

    public R run(Q input, Boolean verbose, Map<String, Object> toolRunKwargs) {

        CallbackManagerForToolRun toolRun = this.callbackManager.onToolStart(this.getName(), input, verbose);

        R result = null;

        try {


            //@todo input if JsonNode

            if (this instanceof FunTool) {
                Class<Q> qq = (Class<Q>) ((FunTool) this).getInputCls();
                result = this._run(JSONUtil.toBean(input.toString(), qq));
            } else {

                Type query = TypeUtil.getTypeArgument(this.getClass());
                Class<Q> cc = (Class<Q>) query;
                result = this._run(JSONUtil.toBean(input.toString(), cc));
            }

            toolRun.onToolEnd(this.getName(), result, input, verbose);

        } catch (Exception e) {

            toolRun.onToolError(e.getMessage(), e);

            throw e;
        }

        return result;
    }


//    public abstract Class<?> getInputCls();


    /**
     * 把类上的第一个范型转换为 JsonSchema
     *
     * @return
     */
    public JsonNode getInputSchemas() {
        Type query = TypeUtil.getTypeArgument(this.getClass());
        Class<Q> cc = (Class<Q>) query;

        return OpenAIUtils.serializeJsonSchema(cc);
    }

}
