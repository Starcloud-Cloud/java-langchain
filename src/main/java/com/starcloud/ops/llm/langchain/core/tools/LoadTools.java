package com.starcloud.ops.llm.langchain.core.tools;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ClassUtil;
import com.starcloud.ops.llm.langchain.core.schema.BaseLanguageModel;
import com.starcloud.ops.llm.langchain.core.callbacks.BaseCallbackHandler;
import com.starcloud.ops.llm.langchain.core.tools.base.BaseTool;
import lombok.Data;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Data
public class LoadTools {


    private final static List<Class<? extends BaseTool>> LLM_TOOLS = Arrays.asList(RequestsGetTool.class, CalculatorTool.class);

    private final static List<Class<? extends BaseTool>> EXTRA_LLM_TOOLS = Arrays.asList(RequestsGetTool.class);

    private final static List<Class<? extends BaseTool>> EXTRA_OPTIONAL_TOOLS = Arrays.asList(RequestsGetTool.class);

    public static List<Class<? extends BaseTool>> loadSystemTools() {

        return null;
    }


    @SneakyThrows
    public static List<BaseTool> loadTools(List<Class<? extends BaseTool>> toolsCls, BaseLanguageModel llm) {

        List<BaseCallbackHandler> callbacks = new ArrayList<>();

        List<BaseTool> tools = new ArrayList<>();
        for (Class<? extends BaseTool> toolCls : toolsCls) {

            BaseTool tool;

            if (LLM_TOOLS.contains(toolCls)) {

                Assert.notNull(llm, "Tool {} requires an LLM to be provided", toolCls.getSimpleName());

                tool = toolCls.getDeclaredConstructor().newInstance();

            } else if (EXTRA_LLM_TOOLS.contains(toolCls)) {

                Assert.notNull(llm, "Tool {} requires an LLM to be provided", toolCls.getSimpleName());

                tool = toolCls.getDeclaredConstructor().newInstance();

            } else if (EXTRA_OPTIONAL_TOOLS.contains(toolCls)) {

                tool = toolCls.getDeclaredConstructor().newInstance();
            } else {
                throw new RuntimeException("Got unknown tool " + toolCls.getSimpleName());
            }

            Optional.ofNullable(callbacks).orElse(new ArrayList<>()).forEach(callbackHandler -> tool.getCallbackManager().addCallbackHandler(callbackHandler));

            tools.add(tool);
        }

        return tools;
    }

    public static List<BaseTool> loadToolsInstance(List<BaseTool> tools, BaseLanguageModel llm) {

        List<BaseCallbackHandler> callbacks = new ArrayList<>();

        return tools;
    }

    protected Boolean checkEffective() {

        return false;
    }

}
