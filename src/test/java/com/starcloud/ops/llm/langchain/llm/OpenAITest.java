package com.starcloud.ops.llm.langchain.llm;

import com.starcloud.ops.llm.langchain.SpringBootTests;
import com.starcloud.ops.llm.langchain.core.chain.conversation.ConversationChain;
import com.starcloud.ops.llm.langchain.core.memory.buffer.ConversationBufferWindowMemory;
import com.starcloud.ops.llm.langchain.core.memory.buffer.ConversationTokenBufferMemory;
import com.starcloud.ops.llm.langchain.core.memory.summary.ConversationSummaryBufferMemory;
import com.starcloud.ops.llm.langchain.core.memory.summary.ConversationSummaryMemory;
import com.starcloud.ops.llm.langchain.core.model.llm.OpenAI;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class OpenAITest extends SpringBootTests {


    @MockBean
    private DataSource dataSource;


    @Test
    public void generateTest() {

        OpenAI llm = new OpenAI();

        log.info("result : {}", llm.call("Hi there! what you name?"));

    }




    @Test
    public void ConversationChainTest() {

        OpenAI llm = new OpenAI();

        ConversationChain<BaseLLMResult> conversationChain = new ConversationChain(llm);
        conversationChain.setVerbose(true);

        BaseLLMResult baseLLMResult = conversationChain.call(Arrays.asList(BaseVariable.newString("input", "Hi there! what you name?")));

        baseLLMResult = conversationChain.call(Arrays.asList(BaseVariable.newString("input", "I'm doing well! Just having a conversation with an AI.")));

    }


    @Test
    public void ConversationBufferWindowMemoryTest() {

        ConversationBufferWindowMemory memory = new ConversationBufferWindowMemory(2);

        memory.getChatHistory().addUserMessage("111hahha12");
        memory.getChatHistory().addAiMessage("11112333");

        memory.getChatHistory().addUserMessage("222hahha12");
        memory.getChatHistory().addAiMessage("22212333");

        memory.getChatHistory().addUserMessage("333hahha12");
        memory.getChatHistory().addAiMessage("33312333");

        memory.getChatHistory().addUserMessage("444hahha12");
        memory.getChatHistory().addAiMessage("444412333");


        List<BaseVariable> baseVariables = memory.loadMemoryVariables();


        log.info("baseVariables: {}", baseVariables);
    }


    @Test
    public void ConversationSummaryMemoryTest() {
        OpenAI llm = new OpenAI();

        ConversationSummaryMemory memory = new ConversationSummaryMemory(llm);
        // memory.setReturnMessages(true);

        memory.saveContext(Arrays.asList(
                BaseVariable.newString("input", "hi")
        ), BaseLLMResult.data("whats up"));

        log.info("loadMemoryVariables: {}", memory.loadMemoryVariables());
    }


    @Test
    public void ChainSummaryMemoryTest() {

        OpenAI llm = new OpenAI();

        ConversationChain<BaseLLMResult> conversationChain = new ConversationChain(llm, new ConversationSummaryMemory(new OpenAI()));
        conversationChain.setVerbose(false);

        BaseLLMResult result = conversationChain.call(Arrays.asList(
                BaseVariable.newString("input", "你们好，你是否能做数学计算？")
        ));

        log.info("baseLLMResult1: {}", result);

        result = conversationChain.call(Arrays.asList(
                BaseVariable.newString("input", "告诉我1+1是多少？")
        ));

        log.info("baseLLMResult2: {}", result);


        result = conversationChain.call(Arrays.asList(
                BaseVariable.newString("input", "告诉我答案在加10等于多少？")
        ));

        log.info("baseLLMResult3: {}", result);
    }


    @Test
    public void ConversationTokenBufferMemoryTest() {

        OpenAI llm = new OpenAI();

        ConversationTokenBufferMemory memory = new ConversationTokenBufferMemory(llm, 12);

        memory.saveContext(Arrays.asList(
                BaseVariable.newString("input", "hi")
        ), BaseLLMResult.data("whats up"));

        memory.saveContext(Arrays.asList(
                BaseVariable.newString("input", "not much you")
        ), BaseLLMResult.data("not much"));

        memory.saveContext(Arrays.asList(
                BaseVariable.newString("input", "what you name")
        ), BaseLLMResult.data("my name df"));

        log.info("baseLLMResult3: {}", memory.loadMemoryVariables());
    }


    @Test
    public void ChainTokenBufferMemoryTest() {

        OpenAI llm = new OpenAI();

        ConversationTokenBufferMemory memory = new ConversationTokenBufferMemory(new OpenAI(), 60);

        ConversationChain<BaseLLMResult> conversationChain = new ConversationChain(llm, memory);
        conversationChain.setVerbose(true);


        BaseLLMResult result = conversationChain.call(Arrays.asList(
                BaseVariable.newString("input", "Hi, what's up?")
        ));

        log.info("baseLLMResult1: {}", result);

        result = conversationChain.call(Arrays.asList(
                BaseVariable.newString("input", "Just working on writing some documentation!")
        ));

        log.info("baseLLMResult2: {}", result);


        result = conversationChain.call(Arrays.asList(
                BaseVariable.newString("input", "For LangChain! Have you heard of it?")
        ));

        log.info("baseLLMResult3: {}", result);


        result = conversationChain.call(Arrays.asList(
                BaseVariable.newString("input", "Haha nope, although a lot of people confuse it for that")
        ));

        log.info("baseLLMResult4: {}", result);

    }


    @Test
    public void ConversationSummaryBufferMemoryTest() {

        OpenAI llm = new OpenAI();

        ConversationSummaryBufferMemory memory = new ConversationSummaryBufferMemory(new OpenAI(), 80);

        ConversationChain<BaseLLMResult> conversationChain = new ConversationChain(llm, memory);
        conversationChain.setVerbose(false);

        BaseLLMResult result = conversationChain.call(Arrays.asList(
                BaseVariable.newString("input", "Hi, what's up?")
        ));

        log.info("baseLLMResult1: {}", result);

        result = conversationChain.call(Arrays.asList(
                BaseVariable.newString("input", "Just working on writing some documentation!")
        ));

        log.info("baseLLMResult2: {}", result);


        result = conversationChain.call(Arrays.asList(
                BaseVariable.newString("input", "For LangChain! Have you heard of it?")
        ));

        log.info("baseLLMResult3: {}", result);


        result = conversationChain.call(Arrays.asList(
                BaseVariable.newString("input", "Haha nope, although a lot of people confuse it for that")
        ));

        log.info("baseLLMResult4: {}", result);

    }


}
