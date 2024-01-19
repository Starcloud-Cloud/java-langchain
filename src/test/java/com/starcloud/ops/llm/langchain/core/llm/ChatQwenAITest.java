package com.starcloud.ops.llm.langchain.core.llm;

import com.starcloud.ops.llm.langchain.core.SpringBootTests;
import com.starcloud.ops.llm.langchain.core.callbacks.StreamingStdOutCallbackHandler;
import com.starcloud.ops.llm.langchain.core.chain.LLMChain;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatQwen;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.model.llm.qwen.Qwen;
import com.starcloud.ops.llm.langchain.core.model.multimodal.qwen.ChatVLQwen;
import com.starcloud.ops.llm.langchain.core.prompt.base.HumanMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.SystemMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.ChatPromptTemplate;
import com.starcloud.ops.llm.langchain.core.schema.message.HumanMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;

@Slf4j
public class ChatQwenAITest extends SpringBootTests {


    @MockBean
    private DataSource dataSource;


    @Test
    public void generateTest() {

        Qwen llm = new Qwen();

        log.info("result : {}", llm.predict("介绍下杭州亚运会的 电竞比赛内容，说下杭州亚运会的反面新闻"));

    }


    @Test
    public void chatTest() {

        ChatQwen chatQwen = new ChatQwen();

        log.info(chatQwen.call(Arrays.asList(new HumanMessage("hi, what you name?"))));

    }


    @Test
    public void chatStreamTest() {

        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        ChatQwen chatQwen = new ChatQwen();
        chatQwen.setVerbose(true);
        chatQwen.setStream(true);

        chatQwen.addCallbackHandler(new StreamingStdOutCallbackHandler(mockHttpServletResponse));

        chatQwen.call(Arrays.asList(new HumanMessage("hi, what you name?")));

    }


    @Test
    public void ChatPromptTemplateTest() {

        ChatPromptTemplate chatPromptTemplate = ChatPromptTemplate.fromMessages(Arrays.asList(

                SystemMessagePromptTemplate.fromTemplate("You are a helpful assistant that translates {input_language} to {output_language}.", "input_language", "output_language"),


                HumanMessagePromptTemplate.fromTemplate("{text}", "text")

        ));


        ChatQwen chatQwen = new ChatQwen();

        LLMChain<BaseLLMResult> llmChain = new LLMChain(chatQwen, chatPromptTemplate);

        llmChain.setVerbose(true);

        String msg = llmChain.call(new HashMap() {{
            put("input_language", "English");
            put("output_language", "French");
            put("text", "I love programming.");
        }}).getText();

        log.info("chatQwen: {}", msg);

    }


    @Test
    public void chatVLTest() {

        ChatVLQwen chatVLQwen = new ChatVLQwen();

        log.info(chatVLQwen.call(Arrays.asList(com.starcloud.ops.llm.langchain.core.schema.message.multimodal.HumanMessage.ofTestImages("图片上画了什么？", "https://dashscope.oss-cn-beijing.aliyuncs.com/images/dog_and_girl.jpeg"))));

    }


}
