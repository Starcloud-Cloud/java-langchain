package com.starcloud.ops.llm.langchain.llm;

import com.starcloud.ops.llm.langchain.SpringBootTests;
import com.starcloud.ops.llm.langchain.core.chain.LLMChain;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;

import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.model.llm.base.ChatResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.HumanMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.PromptValue;
import com.starcloud.ops.llm.langchain.core.prompt.base.SystemMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.*;
import com.starcloud.ops.llm.langchain.core.callbacks.StreamingStdOutCallbackHandler;
import com.starcloud.ops.llm.langchain.core.schema.message.HumanMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.SystemMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;

@Slf4j
public class ChatModelTest extends SpringBootTests {

    @MockBean
    private DataSource dataSource;


    @Test
    public void ChatOpenAICallTest() {

        ChatOpenAI chatOpenAI = new ChatOpenAI();
        chatOpenAI.setVerbose(true);

        chatOpenAI.call(Arrays.asList(new HumanMessage("hi, what you name?")));
    }


    @Test
    public void StreamingStdOutCallbackHandlerTest() {

        MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

        ChatOpenAI chatOpenAI = new ChatOpenAI();
        chatOpenAI.setStream(true);
        chatOpenAI.setVerbose(false);
        chatOpenAI.addCallbackHandler(new StreamingStdOutCallbackHandler(mockHttpServletResponse));

        String msg = chatOpenAI.call(Arrays.asList(new HumanMessage("hi, what you name?")), null);

        log.info("msg: {}", msg);
    }


    @Test
    public void ChatOpenAIGenerateTest() {

        ChatOpenAI chatOpenAI = new ChatOpenAI();
        chatOpenAI.setVerbose(true);

        ChatResult chatResult = chatOpenAI.generate(Arrays.asList(
                Arrays.asList(new SystemMessage("You are a helpful assistant that translates English to French."), new HumanMessage("I love programming.")),

                Arrays.asList(new SystemMessage("You are a helpful assistant that translates English to Chinese."), new HumanMessage("I love artificial intelligence."))
        ), null);

        log.info("chatResult: {}", chatResult);

    }


    @Test
    public void PromptTemplatesTest() {


        ChatPromptTemplate chatPromptTemplate = ChatPromptTemplate.fromMessages(Arrays.asList(

                SystemMessagePromptTemplate.fromTemplate("You are a helpful assistant that translates {input_language} to {output_language}.", "input_language", "output_language"),


                HumanMessagePromptTemplate.fromTemplate("{text}", "text")

        ));

        PromptValue promptValue = chatPromptTemplate.formatPrompt(new HashMap() {{
            put("input_language", "English");
            put("output_language", "French");
            put("text", "I love programming.");
        }});

        log.info("promptValue: {}", promptValue);

    }


    @Test
    public void LLMChainTest() {


        ChatPromptTemplate chatPromptTemplate = ChatPromptTemplate.fromMessages(Arrays.asList(

                SystemMessagePromptTemplate.fromTemplate("You are a helpful assistant that translates {input_language} to {output_language}.", "input_language", "output_language"),


                HumanMessagePromptTemplate.fromTemplate("{text}", "text")

        ));


        ChatOpenAI chatOpenAI = new ChatOpenAI();

        LLMChain<BaseLLMResult> llmChain = new LLMChain(chatOpenAI, chatPromptTemplate);

        llmChain.setVerbose(true);

        String msg = llmChain.call(new HashMap() {{
            put("input_language", "English");
            put("output_language", "French");
            put("text", "I love programming.");
        }}).getText();

        log.info("LLMChain: {}", msg);

    }


}
