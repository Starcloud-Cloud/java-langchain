package com.starcloud.ops.llm.langchain.learning.promptdevelopment.code;

import com.starcloud.ops.llm.langchain.learning.BaseTests;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class Inferring extends BaseTests {

    //评论示例
    private String review = "我需要一盏漂亮的卧室灯，这款灯具有额外的储物功能，价格也不算太高。\n" +
            "我很快就收到了它。在运输过程中，我们的灯绳断了，但是公司很乐意寄送了一个新的。\n" +
            "几天后就收到了。这款灯很容易组装。我发现少了一个零件，于是联系了他们的客服，他们很快就给我寄来了缺失的零件！\n" +
            "在我看来，Lumina 是一家非常关心顾客和产品的优秀公司！";

    @Test
    public void iterative1() {

        String prompt = "以下用三个反引号分隔的产品评论的情感是什么？\n" +
                "评论文本: ```{" + review + "}```";

        String message = this.getCompletion(prompt);

        log.info("iterative1:\n{}", message);
    }


    @Test
    public void iterative2() {

        String prompt = "以下用三个反引号分隔的产品评论的情感是什么？\n" +
                "    用一个单词回答：「正面」或「负面」。\n" +
                "    评论文本: ```{" + review + "}```";

        String message = this.getCompletion(prompt);

        log.info("iterative2:\n{}", message);
    }

    @Test
    public void iterative3() {

        String prompt = "识别以下评论的作者表达的情感。包含不超过五个项目。将答案格式化为以逗号分隔的单词列表。\n" +
                "    评论文本: ```{" + review + "}```";

        String message = this.getCompletion(prompt);

        log.info("iterative3:\n{}", message);
    }

    @Test
    public void iterative4() {

        String prompt = "以下评论的作者是否表达了愤怒？评论用三个反引号分隔。给出是或否的答案。\n" +
                "评论文本: ```{" + review + "}```";

        String message = this.getCompletion(prompt);

        log.info("iterative4:\n{}", message);
    }

    @Test
    public void iterative5() {

        String prompt = "从评论文本中识别以下项目：\n" +
                " - 评论者购买的物品\n" +
                " - 制造该物品的公司\n" +
                "    评论文本用三个反引号分隔。将你的响应格式化为以 “物品” 和 “品牌” 为键的 JSON 对象。\n" +
                "    如果信息不存在，请使用 “未知” 作为值。\n" +
                "    让你的回应尽可能简短。\n" +
                "    评论文本: ```{" + review + "}```";

        String message = this.getCompletion(prompt);

        log.info("iterative5:\n{}", message);
    }


    @Test
    public void iterative6() {

        String prompt = "从评论文本中识别以下项目：\n" +
                "- 情绪（正面或负面）\n" +
                "- 审稿人是否表达了愤怒？（是或否）\n" +
                "- 评论者购买的物品\n" +
                "- 制造该物品的公司\n" +

                "评论用三个反引号分隔。将您的响应格式化为 JSON 对象，以 “Sentiment”、“Anger”、“Item” 和 “Brand” 作为键。\n" +
                "如果信息不存在，请使用 “未知” 作为值。\n" +
                "让你的回应尽可能简短。\n" +
                "将 Anger 值格式化为布尔值。\n" +
                "评论文本: ```{" + review + "}```";

        String message = this.getCompletion(prompt);

        log.info("iterative6:\n{}", message);
    }

    String story = "在政府最近进行的一项调查中，要求公共部门的员工对他们所在部门的满意度进行评分。\n" +
            "    调查结果显示，NASA 是最受欢迎的部门，满意度为 95％。\n" +

            "    一位 NASA 员工 John Smith 对这一发现发表了评论，他表示：\n" +
            "            “我对 NASA 排名第一并不感到惊讶。这是一个与了不起的人们和令人难以置信的机会共事的好地方。我为成为这样一个创新组织的一员感到自豪。”\n" +

            "    NASA 的管理团队也对这一结果表示欢迎，主管 Tom Johnson 表示：\n" +
            "            “我们很高兴听到我们的员工对 NASA 的工作感到满意。\n" +
            "    我们拥有一支才华横溢、忠诚敬业的团队，他们为实现我们的目标不懈努力，看到他们的辛勤工作得到回报是太棒了。”\n" +

            "    调查还显示，社会保障管理局的满意度最低，只有 45％的员工表示他们对工作满意。\n" +
            "    政府承诺解决调查中员工提出的问题，并努力提高所有部门的工作满意度。";

    @Test
    public void iterative7() {

        String prompt = "确定以下给定文本中讨论的五个主题。\n" +
                "每个主题用1-2个单词概括。\n" +
                "输出时用逗号分割每个主题。\n" +
                "给定文本: ```{" + story + "}```";

        String message = this.getCompletion(prompt);

        log.info("iterative7:\n{}", message);
    }


    @Test
    public void iterative8() {

        String prompt = "确定以下给定文本中讨论的五个主题。\n" +
                "每个主题用1-2个单词概括。\n" +
                "输出时用逗号分割每个主题。\n" +
                "给定文本: ```{" + story + "}```";

        String message = this.getCompletion(prompt);

        log.info("iterative8:\n{}", message);
    }

    @Test
    public void iterative9() {

        String prompt = "判断主题列表中的每一项是否是给定文本中的一个话题，\n" +
                "    以列表的形式给出答案，每个主题用 0 或 1。\n" +
                "    主题列表：美国航空航天局、当地政府、工程、员工满意度、联邦政府\n" +
                "    给定文本: ```{" + story + "}```";

        String message = this.getCompletion(prompt);

        log.info("iterative9:\n{}", message);
    }


}
