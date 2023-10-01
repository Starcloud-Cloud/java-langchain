package com.starcloud.ops.llm.langchain.learning.langchain.code;

import com.starcloud.ops.llm.langchain.core.chain.LLMChain;
import com.starcloud.ops.llm.langchain.core.chain.SequentialChain;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;
import com.starcloud.ops.llm.langchain.core.prompt.base.HumanMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.ChatPromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.learning.BaseTests;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;

@Slf4j
public class Chains extends BaseTests {


    @Test
    public void test1() {

        ChatOpenAI chatOpenAI = new ChatOpenAI();

        //这里我们将参数temperature设置为0.0，从而减少生成答案的随机性。
        chatOpenAI.setTemperature(0.0);


        String temp = "描述制造{product}的一个公司的最佳名称是什么?";

        //以human类型创建一个message
        HumanMessagePromptTemplate humanMessagePromptTemplate = HumanMessagePromptTemplate.fromTemplate(temp);

        //创建一个prompt模版
        ChatPromptTemplate chatPromptTemplate = ChatPromptTemplate.fromMessages(Arrays.asList(humanMessagePromptTemplate));


        LLMChain chain = new LLMChain<>(chatOpenAI, chatPromptTemplate);


        chain.run(Arrays.asList(BaseVariable.newString("product", "大号床单套装")));

    }


    @Test
    public void test2() {

        ChatOpenAI chatOpenAI = new ChatOpenAI();

        //这里我们将参数temperature设置为0.0，从而减少生成答案的随机性。
        chatOpenAI.setTemperature(0.0);

        //以human类型创建一个message
        HumanMessagePromptTemplate humanMessagePromptTemplate = HumanMessagePromptTemplate.fromTemplate("把下面的评论review翻译成英文:\n\n{Review}");
        //创建一个prompt模版
        ChatPromptTemplate chatPromptTemplate = ChatPromptTemplate.fromMessages(Arrays.asList(humanMessagePromptTemplate));

        LLMChain chain1 = new LLMChain<>(chatOpenAI, chatPromptTemplate, "English_Review");


        //以human类型创建一个message
        HumanMessagePromptTemplate humanMessagePromptTemplate2 = HumanMessagePromptTemplate.fromTemplate("请你用一句话来总结下面的评论review:\n{English_Review}");
        //创建一个prompt模版
        ChatPromptTemplate chatPromptTemplate2 = ChatPromptTemplate.fromMessages(Arrays.asList(humanMessagePromptTemplate2));

        LLMChain chain2 = new LLMChain<>(chatOpenAI, chatPromptTemplate2, "summary");


        //以human类型创建一个message
        HumanMessagePromptTemplate humanMessagePromptTemplate3 = HumanMessagePromptTemplate.fromTemplate("下面的评论review使用的什么语言:\n{Review}");
        //创建一个prompt模版
        ChatPromptTemplate chatPromptTemplate3 = ChatPromptTemplate.fromMessages(Arrays.asList(humanMessagePromptTemplate3));

        LLMChain chain3 = new LLMChain<>(chatOpenAI, chatPromptTemplate3, "language");


        //以human类型创建一个message
        HumanMessagePromptTemplate humanMessagePromptTemplate4 = HumanMessagePromptTemplate.fromTemplate("使用特定的语言对下面的总结写一个后续回复:\n总结: {summary}\n语言: {language}");
        //创建一个prompt模版
        ChatPromptTemplate chatPromptTemplate4 = ChatPromptTemplate.fromMessages(Arrays.asList(humanMessagePromptTemplate4));

        LLMChain chain4 = new LLMChain<>(chatOpenAI, chatPromptTemplate4, "followup_message");


        SequentialChain sequentialChain = new SequentialChain(Arrays.asList(chain1, chain2, chain3, chain4), Arrays.asList("Review"), Arrays.asList("English_Review", "summary", "followup_message"));
        sequentialChain.setVerbose(true);

        String review = "Je trouve le goût médiocre. La mousse ne tient pas, c'est bizarre. J'achète les mêmes dans le commerce et le goût est bien meilleur... Vieux lot ou contrefaçon !?";
        String result = sequentialChain.run(Arrays.asList(BaseVariable.newString("Review", review)));

        log.info("test2: {}", result);

    }

}
