package com.starcloud.ops.llm.langchain.core.tools;

import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.starcloud.ops.llm.langchain.core.tools.base.BaseTool;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;


/**
 * http get 请求工具
 */
@Slf4j
@Data
public class CalculatorTool extends BaseTool<CalculatorTool.Request, Number> {


    private String name = "CalculatorTool";

    private String description = "Useful for when you need to answer questions about math.Translate a math problem into a expression that can be executed using javascript. Use the output of running this code to answer the question";


    @SneakyThrows
    @Override
    protected Number _run(Request input) {

        log.info("CalculatorTool: {}", input.getQuery());
        ExpressionParser parser = new SpelExpressionParser();

        Expression exp = parser.parseExpression(input.getQuery());
        Number result = NumberUtil.parseNumber(String.valueOf(exp.getValue()));

//        ScriptEngineManager manager = new ScriptEngineManager();
//        ScriptEngine engine = manager.getEngineByExtension("js");
//
//        Object result = engine.eval(input.getQuery());

        return result;
    }


    @Data
    public static class Request {

        @JsonProperty(required = true)
        @JsonPropertyDescription("single line mathematical expression that solves the problem. Examples: 37593 * 67, 37593 * 67.")
        private String query;

    }
}
