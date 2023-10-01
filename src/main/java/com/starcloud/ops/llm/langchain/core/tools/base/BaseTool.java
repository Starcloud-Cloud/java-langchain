package com.starcloud.ops.llm.langchain.core.tools.base;

import cn.hutool.core.util.TypeUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.llm.langchain.core.callbacks.BaseCallbackManager;
import com.starcloud.ops.llm.langchain.core.callbacks.CallbackManager;
import com.starcloud.ops.llm.langchain.core.callbacks.CallbackManagerForToolRun;
import com.starcloud.ops.llm.langchain.core.tools.utils.OpenAIUtils;
import kotlin.jvm.Transient;
import lombok.Data;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Data
public abstract class BaseTool<Q> {

    private String name;

    private String description;

    private Boolean verbose;

    private Boolean returnDirect = false;

    @Transient
    private BaseCallbackManager callbackManager = new CallbackManager();

    protected abstract ToolResponse _run(Q input);

    public ToolResponse run(Q input) {
        return this.run(input, false, new HashMap<>());
    }

    public ToolResponse run(Q input, Boolean verbose, Map<String, Object> toolRunKwargs) {

        CallbackManagerForToolRun toolRun = this.callbackManager.onToolStart(this.getName(), input, verbose);

        ToolResponse result = null;

        try {

            if (this instanceof FunTool) {
                Class<Q> qq = (Class<Q>) ((FunTool) this).getInputCls();
                result = this._run(input);

            } else {

                Type query = TypeUtil.getTypeArgument(this.getClass());
                Class<Q> cc = (Class<Q>) query;
                result = this._run(input);
            }

            toolRun.onToolEnd(this.getName(), result, input, verbose);

        } catch (Exception e) {

            toolRun.onToolError(e.getMessage(), e);

            throw e;
        }

        return result;
    }

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
