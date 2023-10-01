package com.starcloud.ops.llm.langchain.learning.promptdevelopment.code;

import com.starcloud.ops.llm.langchain.learning.BaseTests;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class Transforming extends BaseTests {


    @Test
    public void iterative1() {

        String prompt = "将以下中文翻译成西班牙语: \n" +
                "```您好，我想订购一个搅拌机。```";

        String message = this.getCompletion(prompt);

        log.info("iterative1:\n{}", message);
    }

    @Test
    public void iterative2() {

        String prompt = "请告诉我以下文本是什么语种: \n" +
                "```Combien coûte le lampadaire?```";

        String message = this.getCompletion(prompt);

        log.info("iterative2:\n{}", message);
    }

    @Test
    public void iterative3() {

        String prompt = "请将以下文本分别翻译成中文、英文、法语和西班牙语:\n" +
                "```I want to order a basketball.```";

        String message = this.getCompletion(prompt);

        log.info("iterative3:\n{}", message);
    }

    @Test
    public void iterative4() {

        String prompt = "请将以下文本翻译成中文，分别展示成正式与非正式两种语气:\n" +
                "```Would you like to order a pillow?```";

        String message = this.getCompletion(prompt);

        log.info("iterative4:\n{}", message);
    }


    private List<String> messages = Arrays.asList(
            "La performance du système est plus lente que d'habitude.",
            "Mi monitor tiene píxeles que no se iluminan.",
            "Il mio mouse non funziona",
            "Mój klawisz Ctrl jest zepsuty",
            "我的屏幕在闪烁"
    );

    @Test
    public void iterative5() {

        for (String issue : messages) {
            String prompt = "告诉我以下文本是什么语种，直接输出语种，如法语，无需输出标点符号: ```{" + issue + "}```";
            String lang = this.getCompletion(prompt);
            log.info("\n原始消息: （{}） {}", lang, issue);

            String prompt2 = "将以下消息分别翻译成英文和中文，并写成\n" +
                    "    中文翻译：xxx\n" +
                    "    英文翻译：yyy\n" +
                    "    的格式：\n" +
                    "    ```{" + issue + "}```";
            String str = this.getCompletion(prompt2);
            log.info("\n{}\n================================================================================================", str);

        }

    }


    @Test
    public void iterative6() {

        String prompt = "将以下文本翻译成商务信函的格式:\n" +
                "```小老弟，我小羊，上回你说咱部门要采购的显示器是多少寸来着？```";

        String message = this.getCompletion(prompt);

        log.info("iterative6:\n{}", message);

    }

    @Test
    public void iterative7() {


        String json = "{ \"resturant employees\" :[\n" +
                "        {\"name\":\"Shyam\", \"email\":\"shyamjaiswal@gmail.com\"},\n" +
                "        {\"name\":\"Bob\", \"email\":\"bob32@gmail.com\"},\n" +
                "        {\"name\":\"Jai\", \"email\":\"jai87@gmail.com\"}\n" +
                "]}";


        String prompt = "将以下JSON转换为HTML表格，保留表格标题和列名：" + json + "";

        String message = this.getCompletion(prompt);

        log.info("iterative7:\n{}", message);

    }


    @Test
    public void iterative8() {


        String text = "Got this for my daughter for her birthday cuz she keeps taking mine from my room.  Yes, adults also like pandas too.  She takes it everywhere with her, and it's super soft and cute.  " +
                "One of the ears is a bit lower than the other, and I don't think that was designed to be asymmetrical. It's a bit small for what I paid for it though. I think there might be other options that are bigger for the same price.  " +
                "It arrived a day earlier than expected, so I got to play with it myself before I gave it to my daughter.";


        String prompt = "针对以下三个反引号之间的英文评论文本，\n" +
                "首先进行拼写及语法纠错，\n" +
                "然后将其转化成中文，\n" +
                "再将其转化成优质淘宝评论的风格，从各种角度出发，分别说明产品的优点与缺点，并进行总结。\n" +
                "润色一下描述，使评论更具有吸引力。\n" +
                "输出结果格式为：\n" +
                "【优点】xxx\n" +
                "【缺点】xxx\n" +
                "【总结】xxx\n" +
                "注意，只需填写xxx部分，并分段输出。\n" +
                "将结果输出成Markdown格式。\n" +
                "```{" + text + "}```";

        String message = this.getCompletion(prompt);

        log.info("iterative8:\n{}", message);

    }


}
