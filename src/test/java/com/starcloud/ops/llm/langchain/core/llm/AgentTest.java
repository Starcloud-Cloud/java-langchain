package com.starcloud.ops.llm.langchain.core.llm;

import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.starcloud.ops.llm.langchain.core.SpringBootTests;
import com.starcloud.ops.llm.langchain.config.SerpAPIToolConfig;
import com.starcloud.ops.llm.langchain.core.agent.OpenAIFunctionsAgent;
import com.starcloud.ops.llm.langchain.core.agent.base.AgentExecutor;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;
import com.starcloud.ops.llm.langchain.core.schema.message.HumanMessage;
import com.starcloud.ops.llm.langchain.core.tools.LoadTools;
import com.starcloud.ops.llm.langchain.core.tools.RequestsGetTool;
import com.starcloud.ops.llm.langchain.core.tools.SerpAPITool;
import com.starcloud.ops.llm.langchain.core.tools.base.BaseTool;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;


@ImportAutoConfiguration(classes = SerpAPIToolConfig.class)
@Slf4j
public class AgentTest extends SpringBootTests {

    @MockBean
    private DataSource dataSource;

    @Autowired
    private SerpAPIToolConfig serpAPIToolConfig;

    @Test
    public void generateToolTest() {

        ChatOpenAI chatOpenAI = new ChatOpenAI();

        chatOpenAI.setModel("gpt-3.5-turbo-0613");

        String out = chatOpenAI.call(Arrays.asList(new HumanMessage("hi, what you name?")));

        log.info("out: {}", out);
    }

    @Test
    public void loadToolsTest() {

        ChatOpenAI chatOpenAI = new ChatOpenAI();

        List<BaseTool> tools = LoadTools.loadTools(Arrays.asList(RequestsGetTool.class), chatOpenAI);

        log.info("tools: {}", JSONUtil.parse(tools, JSONConfig.create()).toStringPretty());

    }


    @Test
    public void initAgentTest() {

        ChatOpenAI chatOpenAI = new ChatOpenAI();

        List<BaseTool> tools = LoadTools.loadTools(Arrays.asList(RequestsGetTool.class), chatOpenAI);

        OpenAIFunctionsAgent baseSingleActionAgent = OpenAIFunctionsAgent.fromLLMAndTools(chatOpenAI, tools);

        AgentExecutor agentExecutor = AgentExecutor.fromAgentAndTools(tools, chatOpenAI, baseSingleActionAgent, baseSingleActionAgent.getCallbackManager());

        agentExecutor.run("Who is Leo DiCaprio's girlfriend? What is her current age raised to the 0.43 power?");

        log.info("tools: {}", JSONUtil.parse(tools).toStringPretty());

    }


    @Test
    public void SerpapiToolTest() {

        ChatOpenAI chatOpenAI = new ChatOpenAI();
        chatOpenAI.setModel("gpt-4-0613"); //gpt-3.5-turbo-0613, gpt-4-0613

        List<BaseTool> tools = LoadTools.loadTools(Arrays.asList(), chatOpenAI);


        tools.add(new SerpAPITool(serpAPIToolConfig.getApiKey()));

        OpenAIFunctionsAgent baseSingleActionAgent = OpenAIFunctionsAgent.fromLLMAndTools(chatOpenAI, tools);

        AgentExecutor agentExecutor = AgentExecutor.fromAgentAndTools(tools, chatOpenAI, baseSingleActionAgent, baseSingleActionAgent.getCallbackManager());

        agentExecutor.run("Who is Leo DiCaprio's girlfriend Or ex-girlfriend? What is her current age raised to the 0.43 power?");

        log.info("tools: {}", JSONUtil.parse(tools).toStringPretty());

    }

}
