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

        log.info(chatVLQwen.call(Arrays.asList(com.starcloud.ops.llm.langchain.core.schema.message.multimodal.HumanMessage.ofImages("图片上画了什么？", Arrays.asList("https://dashscope.oss-cn-beijing.aliyuncs.com/images/dog_and_girl.jpeg")))));

    }


    @Test
    public void chatVLPromptTest() {

        ChatVLQwen chatVLQwen = new ChatVLQwen();
        chatVLQwen.setTopP(0.9D);

        log.info(chatVLQwen.call(Arrays.asList(com.starcloud.ops.llm.langchain.core.schema.message.multimodal.HumanMessage.
                                ofImages("根据标题：```春风拂面，万物复苏。```为每张图片配上符合图片意境的标题，标题在20个字内!!!",
                                        Arrays.asList("https://download.mofaai.com.cn/mofaai/images/upload/df43d3533e54449aafb40d8f641441d9.jpg?x-oss-process=image/resize,m_lfit,w_448,h_448",
                                                "https://download.mofaai.com.cn/mofaai/images/upload/16c9927c7bfe4c15bfd443b5850185a1.jpg?x-oss-process=image/resize,m_lfit,w_448,h_448")

                                )

                        )
                )
        );


    }


    @Test
    public void chatVLPromptV2Test() {

        ChatVLQwen chatVLQwen = new ChatVLQwen();
        chatVLQwen.setTopP(0.9D);

        //汉堡图
//        log.info(chatVLQwen.call(Arrays.asList(com.starcloud.ops.llm.langchain.core.schema.message.multimodal.HumanMessage.
//                                ofImages("描述下图片的时间,地点,人物,场景和意境，不要增加图片上没有的东西！！！",
//                                        Arrays.asList("https://download.hotsalecloud.com/mofaai/images/upload/d49b75339e8542a09b66bf71d04f541c.jpg?x-oss-process=image/resize,m_lfit,w_448,h_448"))
//
//                        )
//                )
//        );

        //汉堡图
        log.info(chatVLQwen.call(Arrays.asList(com.starcloud.ops.llm.langchain.core.schema.message.multimodal.HumanMessage.
                                ofImages("为图片配上一个符合图片场景和意境的标题和副标题，标题在5个中文字内，让人看到有购买欲。输出格式：```" +
                                                "标题: \r\n" +
                                                "副标题: ```",
                                        Arrays.asList("https://download.hotsalecloud.com/mofaai/images/upload/d49b75339e8542a09b66bf71d04f541c.jpg?x-oss-process=image/resize,m_lfit,w_448,h_448"))

                        )
                )
        );


    }

}
