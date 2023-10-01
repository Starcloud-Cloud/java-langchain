package com.starcloud.ops.llm.langchain.learning.promptdevelopment.code;

import com.starcloud.ops.llm.langchain.learning.BaseTests;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class Guidelines extends BaseTests {


    @Test
    public void guidelines1() {


        String message = this.getCompletion("hi, what you name?");

        log.info("guidelines1:\n{}", message);
    }


    @Test
    public void guidelines2() {

        String text = "您应该提供尽可能清晰、具体的指示，以表达您希望模型执行的任务。\n" +
                "这将引导模型朝向所需的输出，并降低收到无关或不正确响应的可能性。\n" +
                "不要将写清晰的提示词与写简短的提示词混淆。\n" +
                "在许多情况下，更长的提示词可以为模型提供更多的清晰度和上下文信息，从而导致更详细和相关的输出";


        String prompt = "把用三个反引号括起来的文本总结成一句话。" +
                "```" + text + "```";

        String message = this.getCompletion(prompt);

        log.info("guidelines2:{}", message);
    }


    @Test
    public void guidelines3() {

        String prompt = "请生成包括书名、作者和类别的三本虚构书籍清单，并以 JSON 格式提供，其中包含以下键:book_id、title、author、genre。";

        String message = this.getCompletion(prompt);

        log.info("guidelines3:{}", message);
    }

    @Test
    public void guidelines4() {

        String test = "泡一杯茶很容易。首先，需要把水烧开。\n" +
                "在等待期间，拿一个杯子并把茶包放进去。\n" +
                "一旦水足够热，就把它倒在茶包上。\n" +
                "等待一会儿，让茶叶浸泡。几分钟后，取出茶包。\n" +
                "如果您愿意，可以加一些糖或牛奶调味。\n" +
                "就这样，您可以享受一杯美味的茶了。";

        String prompt = "您将获得由三个引号括起来的文本。\n" +
                "如果它包含一系列的指令，则需要按照以下格式重新编写这些指令：\n" +
                "\n" +
                "第一步 - ...\n" +
                "第二步 - …\n" +
                "…\n" +
                "第N步 - …\n" +
                "\n" +
                "如果文本中不包含一系列的指令，则直接写“未提供步骤”。\n" +
                "\n" +
                "```" + test + "```";

        String message = this.getCompletion(prompt);

        log.info("\nguidelines4-1:\n{}", message);


        //不满足条件，内容中没有提供步骤信息
        String test2 = "今天阳光明媚，鸟儿在歌唱。\n" +
                "这是一个去公园散步的美好日子。\n" +
                "鲜花盛开，树枝在微风中轻轻摇曳。\n" +
                "人们外出享受着这美好的天气，有些人在野餐，有些人在玩游戏或者在草地上放松。\n" +
                "这是一个完美的日子，可以在户外度过并欣赏大自然的美景";

        String prompt2 = "您将获得由三个引号括起来的文本。\n" +
                "如果它包含一系列的指令，则需要按照以下格式重新编写这些指令：\n" +
                "\n" +
                "第一步 - ...\n" +
                "第二步 - …\n" +
                "…\n" +
                "第N步 - …\n" +
                "\n" +
                "如果文本中不包含一系列的指令，则直接写“未提供步骤”。\n" +
                "\n" +
                "```" + test2 + "```";

        String message2 = this.getCompletion(prompt2);

        log.info("\nguidelines4-2:\n{}", message2);

    }


    @Test
    public void guidelines5() {

        String prompt = "您的任务是以一致的风格回答问题。\n" +
                "\n" +
                "<孩子>: 教我耐心。\n" +
                "\n" +
                "<祖父母>: 挖出最深峡谷的河流源于一处不起眼的泉眼；最宏伟的交响乐从单一的音符开始；最复杂的挂毯以一根孤独的线开始编织。\n" +
                "\n" +
                "<孩子>: 教我韧性。";

        String message = this.getCompletion(prompt);

        log.info("\nguidelines5:\n{}", message);

    }


    @Test
    public void guidelines6() {

        String test = "在一个迷人的村庄里，兄妹杰克和吉尔出发去一个山顶井里打水。\n" +
                "他们一边唱着欢乐的歌，一边往上爬，\n" +
                "然而不幸降临——杰克绊了一块石头，从山上滚了下来，吉尔紧随其后。\n" +
                "虽然略有些摔伤，但他们还是回到了温馨的家中。\n" +
                "尽管出了这样的意外，他们的冒险精神依然没有减弱，继续充满愉悦地探索。";


        String prompt = "执行以下操作：\n" +
                "1-用一句话概括下面用三个反引号括起来的文本。\n" +
                "2-将摘要翻译成法语。\n" +
                "3-在法语摘要中列出每个人名。\n" +
                "4-输出一个 JSON 对象，其中包含以下键：French_summary，num_names。\n" +
                "\n" +
                "请用换行符分隔您的答案。\n" +
                "\n" +
                "Text:\n" +
                "```" + test + "```";

        String message = this.getCompletion(prompt);

        log.info("\nguidelines6:\n{}", message);

    }


    @Test
    public void guidelines7() {

        String test = "在一个迷人的村庄里，兄妹杰克和吉尔出发去一个山顶井里打水。\n" +
                "他们一边唱着欢乐的歌，一边往上爬，\n" +
                "然而不幸降临——杰克绊了一块石头，从山上滚了下来，吉尔紧随其后。\n" +
                "虽然略有些摔伤，但他们还是回到了温馨的家中。\n" +
                "尽管出了这样的意外，他们的冒险精神依然没有减弱，继续充满愉悦地探索。";


        String prompt = "1-用一句话概括下面用<>括起来的文本。\n" +
                "2-将摘要翻译成英语。\n" +
                "3-在英语摘要中列出每个名称。\n" +
                "4-输出一个 JSON 对象，其中包含以下键：English_summary，num_names。\n" +
                "\n" +
                "请使用以下格式：\n" +
                "文本：<要总结的文本>\n" +
                "摘要：<摘要>\n" +
                "翻译：<摘要的翻译>\n" +
                "名称：<英语摘要中的名称列表>\n" +
                "输出 JSON：<带有 English_summary 和 num_names 的 JSON>\n" +
                "\n" +
                "Text: <" + test + ">";

        String message = this.getCompletion(prompt);

        log.info("\nguidelines7:\n{}", message);

    }


    @Test
    public void guidelines81() {

        String prompt = "判断学生的解决方案是否正确。\n" +
                "\n" +
                "问题:\n" +
                "我正在建造一个太阳能发电站，需要帮助计算财务。\n" +
                "\n" +
                "    土地费用为 100美元/平方英尺\n" +
                "    我可以以 250美元/平方英尺的价格购买太阳能电池板\n" +
                "    我已经谈判好了维护合同，每年需要支付固定的10万美元，并额外支付每平方英尺10美元\n" +
                "    作为平方英尺数的函数，首年运营的总费用是多少。\n" +
                "\n" +
                "学生的解决方案：\n" +
                "设x为发电站的大小，单位为平方英尺。\n" +
                "费用：\n" +
                "\n" +
                "    土地费用：100x\n" +
                "    太阳能电池板费用：250x\n" +
                "    维护费用：100,000美元+100x\n" +
                "    总费用：100x+250x+100,000美元+100x=450x+100,000美元";

        String message = this.getCompletion(prompt);

        log.info("\nguidelines8-1:\n{}", message);

    }

    @Test
    public void guidelines82() {

        String prompt = "请判断学生的解决方案是否正确，请通过如下步骤解决这个问题：\n" +
                "\n" +
                "步骤：\n" +
                "\n" +
                "    首先，自己解决问题。\n" +
                "    然后将您的解决方案与学生的解决方案进行比较，并评估学生的解决方案是否正确。\n" +
                "    在自己完成问题之前，请勿决定学生的解决方案是否正确。\n" +
                "\n" +
                "使用以下格式：\n" +
                "\n" +
                "    问题：问题文本\n" +
                "    学生的解决方案：学生的解决方案文本\n" +
                "    实际解决方案和步骤：实际解决方案和步骤文本\n" +
                "    学生的解决方案和实际解决方案是否相同：是或否\n" +
                "    学生的成绩：正确或不正确\n" +
                "\n" +
                "问题：\n" +
                "\n" +
                "    我正在建造一个太阳能发电站，需要帮助计算财务。 \n" +
                "    - 土地费用为每平方英尺100美元\n" +
                "    - 我可以以每平方英尺250美元的价格购买太阳能电池板\n" +
                "    - 我已经谈判好了维护合同，每年需要支付固定的10万美元，并额外支付每平方英尺10美元\n" +
                "    作为平方英尺数的函数，首年运营的总费用是多少。\n" +
                "\n" +
                "学生的解决方案：\n" +
                "\n" +
                "    设x为发电站的大小，单位为平方英尺。\n" +
                "    费用：\n" +
                "    1. 土地费用：100x\n" +
                "    2. 太阳能电池板费用：250x\n" +
                "    3. 维护费用：100,000+100x\n" +
                "    总费用：100x+250x+100,000+100x=450x+100,000\n" +
                "\n" +
                "实际解决方案和步骤：";

        String message = this.getCompletion(prompt);

        log.info("\nguidelines8-2:\n{}", message);

    }


    @Test
    public void guidelines9() {

        String prompt = "告诉我 Boie 公司生产的 AeroGlide UltraSlim Smart Toothbrush 的相关信息";

        String message = this.getCompletion(prompt);

        log.info("\nguidelines9:\n{}", message);

    }


}
