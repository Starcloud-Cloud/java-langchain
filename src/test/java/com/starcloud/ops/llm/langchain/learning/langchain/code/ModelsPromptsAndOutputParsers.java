package com.starcloud.ops.llm.langchain.learning.langchain.code;

import cn.hutool.json.JSONUtil;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;
import com.starcloud.ops.llm.langchain.core.prompt.base.ChatPromptValue;
import com.starcloud.ops.llm.langchain.core.prompt.base.HumanMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.ChatPromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.message.HumanMessage;
import com.starcloud.ops.llm.langchain.learning.BaseTests;
import com.theokanning.openai.moderation.Moderation;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;

@Slf4j
public class ModelsPromptsAndOutputParsers extends BaseTests {


    @Test
    public void test1() {

        String prompt = "1+1是什么？";

        String result = this.getCompletion(prompt);

        log.info("test1:\n{}", result);
    }


    @Test
    public void test2() {

        String customerEmail = "嗯呐，我现在可是火冒三丈，我那个搅拌机盖子竟然飞了出去，把我厨房的墙壁都溅上了果汁！\n" +
                "更糟糕的是，保修条款可不包括清理我厨房的费用。\n" +
                "伙计，赶紧给我过来！";

        String style = "正式普通话用一个平静、尊敬、有礼貌的语调";


        String prompt = "把由三个反引号分隔的文本\n" +
                "翻译成一种" + style + "风格。\n" +
                "文本: ```" + customerEmail + "```";

        log.info("prompt:\n{}", prompt);

        String result = this.getCompletion(prompt);

        log.info("test2:\n{}", result);
    }


    @Test
    public void test3() {

        ChatOpenAI chatOpenAI = new ChatOpenAI();
        chatOpenAI.setTemperature(0.0);


        log.info("test3:\n{}", chatOpenAI);
    }

    @Test
    public void test4() {

        String temp = "把由三个反引号分隔的文本\n" +
                "翻译成一种{style}风格。\n" +
                "文本: ```{text}```";

        //以human类型创建一个message
        HumanMessagePromptTemplate humanMessagePromptTemplate = HumanMessagePromptTemplate.fromTemplate(temp);

        //创建一个prompt模版
        ChatPromptTemplate chatPromptTemplate = ChatPromptTemplate.fromMessages(Arrays.asList(humanMessagePromptTemplate));

        //传入参数，生成最后完整的替换过变量的prompt
        ChatPromptValue chatPromptValue = chatPromptTemplate.formatPrompt(Arrays.asList(
                BaseVariable.newString("style", "正式普通话 \n" +
                        "用一个平静、尊敬的语气"),
                BaseVariable.newString("text", "嗯呐，我现在可是火冒三丈，我那个搅拌机盖子竟然飞了出去，把我厨房的墙壁都溅上了果汁！\n" +
                        "更糟糕的是，保修条款可不包括清理我厨房的费用。\n" +
                        "伙计，赶紧给我过来！")
        ));

        log.info("test4:\n{}", chatPromptValue);


        ChatOpenAI chatOpenAI = new ChatOpenAI();
        chatOpenAI.setTemperature(0.0);

        String result = chatOpenAI.call(chatPromptValue);

        log.info("result:\n{}", result);

    }


}
