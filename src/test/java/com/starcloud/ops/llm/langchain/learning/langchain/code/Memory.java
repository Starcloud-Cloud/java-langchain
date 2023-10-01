package com.starcloud.ops.llm.langchain.learning.langchain.code;

import com.starcloud.ops.llm.langchain.core.chain.conversation.ConversationChain;
import com.starcloud.ops.llm.langchain.core.memory.buffer.ConversationBufferMemory;
import com.starcloud.ops.llm.langchain.core.memory.buffer.ConversationBufferWindowMemory;
import com.starcloud.ops.llm.langchain.core.memory.buffer.ConversationTokenBufferMemory;
import com.starcloud.ops.llm.langchain.core.memory.summary.ConversationSummaryBufferMemory;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;
import com.starcloud.ops.llm.langchain.core.model.llm.OpenAI;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.learning.BaseTests;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class Memory extends BaseTests {


    @Test
    public void test1() {

        ChatOpenAI chatOpenAI = new ChatOpenAI();

        //这里我们将参数temperature设置为0.0，从而减少生成答案的随机性。
        chatOpenAI.setTemperature(0.0);

        ConversationBufferMemory memory = new ConversationBufferMemory();

        //新建一个 ConversationChain Class 实例
        ConversationChain chain = new ConversationChain(chatOpenAI, memory);

        chain.run("你好, 我叫皮皮鲁");


        chain.run("1+1等于多少？");


        chain.run("我叫什么名字？");


        String buffer = memory.getBuffer();

        log.info("buffer: {}", buffer);

        log.info("loadMemoryVariables: {}", memory.loadMemoryVariables());


    }


    @Test
    public void test2() {


        ConversationBufferMemory memory = new ConversationBufferMemory();

        memory.saveContext(BaseVariable.newString("input", "你好，我叫皮皮鲁"), BaseVariable.newString("output", "你好啊，我叫鲁西西"));

        log.info("loadMemoryVariables: {}", memory.loadMemoryVariables());


        memory.saveContext(BaseVariable.newString("input", "很高兴和你成为朋友！"), BaseVariable.newString("output", "是的，让我们一起去冒险吧！"));

        log.info("loadMemoryVariables: {}", memory.loadMemoryVariables());


    }


    @Test
    public void test3() {


        ConversationBufferWindowMemory memory = new ConversationBufferWindowMemory(1);

        memory.saveContext(BaseVariable.newString("input", "你好，我叫皮皮鲁"), BaseVariable.newString("output", "你好啊，我叫鲁西西"));

        log.info("loadMemoryVariables: {}", memory.loadMemoryVariables());


        memory.saveContext(BaseVariable.newString("input", "很高兴和你成为朋友！"), BaseVariable.newString("output", "是的，让我们一起去冒险吧！"));

        log.info("loadMemoryVariables: {}", memory.loadMemoryVariables());


    }


    @Test
    public void test4() {

        ChatOpenAI chatOpenAI = new ChatOpenAI();
        //这里我们将参数temperature设置为0.0，从而减少生成答案的随机性。
        chatOpenAI.setTemperature(0.0);

        ConversationBufferWindowMemory memory = new ConversationBufferWindowMemory(1);

        //新建一个 ConversationChain Class 实例
        ConversationChain chain = new ConversationChain(chatOpenAI, memory);


        log.info("第一轮");
        chain.run("你好, 我叫皮皮鲁");

        log.info("第二轮");
        chain.run("1+1等于多少？");

        log.info("第三轮");
        chain.run("我叫什么名字？");


    }


    @Test
    public void test5() {

        ChatOpenAI chatOpenAI = new ChatOpenAI();
        //这里我们将参数temperature设置为0.0，从而减少生成答案的随机性。
        chatOpenAI.setTemperature(0.0);

        ConversationTokenBufferMemory memory = new ConversationTokenBufferMemory(chatOpenAI, 30);

        memory.saveContext(BaseVariable.newString("input", "朝辞白帝彩云间，"), BaseVariable.newString("output", "千里江陵一日还。"));

        memory.saveContext(BaseVariable.newString("input", "两岸猿声啼不住，"), BaseVariable.newString("output", "轻舟已过万重山。"));


        log.info("loadMemoryVariables: {}", memory.loadMemoryVariables());

    }


    @Test
    public void test6() {

        String text = "在八点你和你的产品团队有一个会议。\n" +
                "你需要做一个PPT。\n" +
                "上午9点到12点你需要忙于LangChain。\n" +
                "Langchain是一个有用的工具，因此你的项目进展的非常快。\\\n" +
                "中午，在意大利餐厅与一位开车来的顾客共进午餐\n" +
                "走了一个多小时的路程与你见面，只为了解最新的 AI。\n" +
                "确保你带了笔记本电脑可以展示最新的 LLM 样例.";

        ChatOpenAI chatOpenAI = new ChatOpenAI();
        //这里我们将参数temperature设置为0.0，从而减少生成答案的随机性。
        chatOpenAI.setTemperature(0.0);

        ConversationSummaryBufferMemory memory = new ConversationSummaryBufferMemory(chatOpenAI, 100);

        memory.saveContext(BaseVariable.newString("input", "你好，我叫皮皮鲁"), BaseVariable.newString("output", "你好啊，我叫鲁西西"));

        memory.saveContext(BaseVariable.newString("input", "很高兴和你成为朋友！"), BaseVariable.newString("output", "是的，让我们一起去冒险吧！"));

        memory.saveContext(BaseVariable.newString("input", "今天的日程安排是什么？"), BaseVariable.newString("output", text));


        log.info("getMovingSummaryBuffer: {}", memory.getMovingSummaryBuffer());


        //新建一个 ConversationChain Class 实例
        ConversationChain chain = new ConversationChain(chatOpenAI, memory);

        log.info("result :{}", chain.run("展示什么样的样例最好呢？"));


        log.info("getMovingSummaryBuffer: {}", memory.getMovingSummaryBuffer());


    }

}
