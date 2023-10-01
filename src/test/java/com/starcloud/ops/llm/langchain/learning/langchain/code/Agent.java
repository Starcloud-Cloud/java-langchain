package com.starcloud.ops.llm.langchain.learning.langchain.code;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.starcloud.ops.llm.langchain.core.agent.OpenAIFunctionsAgent;
import com.starcloud.ops.llm.langchain.core.agent.base.AgentExecutor;
import com.starcloud.ops.llm.langchain.core.chain.LLMChain;
import com.starcloud.ops.llm.langchain.core.chain.SequentialChain;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;
import com.starcloud.ops.llm.langchain.core.tools.CalculatorTool;
import com.starcloud.ops.llm.langchain.core.tools.LoadTools;
import com.starcloud.ops.llm.langchain.core.tools.RequestsGetTool;
import com.starcloud.ops.llm.langchain.core.tools.base.BaseTool;
import com.starcloud.ops.llm.langchain.core.tools.base.FunTool;
import com.starcloud.ops.llm.langchain.core.tools.base.ToolResponse;
import com.starcloud.ops.llm.langchain.learning.BaseTests;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class Agent extends BaseTests {


    @Test
    public void test1() {

        ChatOpenAI chatOpenAI = new ChatOpenAI();

        //这里我们将参数temperature设置为0.0，从而减少生成答案的随机性。
        chatOpenAI.setTemperature(0.0);


        List<BaseTool> tools = LoadTools.loadTools(Arrays.asList(CalculatorTool.class), chatOpenAI);

        OpenAIFunctionsAgent baseSingleActionAgent = OpenAIFunctionsAgent.fromLLMAndTools(chatOpenAI, tools);

        AgentExecutor agentExecutor = AgentExecutor.fromAgentAndTools(baseSingleActionAgent, tools, chatOpenAI.getCallbackManager());

        agentExecutor.run("计算300的25%");

    }


    @Test
    public void test2() {

        ChatOpenAI chatOpenAI = new ChatOpenAI();

        //这里我们将参数temperature设置为0.0，从而减少生成答案的随机性。
        chatOpenAI.setTemperature(0.0);

        MySelfTool mySelfTool = new MySelfTool();

        List<BaseTool> tools = LoadTools.loadToolsInstance(Arrays.asList(mySelfTool), chatOpenAI);

        OpenAIFunctionsAgent baseSingleActionAgent = OpenAIFunctionsAgent.fromLLMAndTools(chatOpenAI, tools);

        AgentExecutor agentExecutor = AgentExecutor.fromAgentAndTools(baseSingleActionAgent, tools, chatOpenAI.getCallbackManager());

        agentExecutor.run("今天的日期是？");

    }

    @Data
    public static class MySelfTool extends BaseTool<MySelfTool.Request> {

        private String name = "MySelfTool";

        private String description = "返回今天的日期，用于任何需要知道今天日期的问题, 输入应该总是一个空字符串,这个函数将总是返回今天的日期，任何日期计算应该在这个函数之外进行。";

        @Override
        protected ToolResponse _run(Request input) {

            return ToolResponse.buildObservation(DateUtil.now());
        }


        @Data
        public static class Request {

            private String query;

        }
    }

}
