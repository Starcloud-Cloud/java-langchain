package com.starcloud.ops.llm.langchain.learning.promptdevelopment.code;

import com.starcloud.ops.llm.langchain.learning.BaseTests;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class Iterative extends BaseTests {

    //产品说明书
    private String sheet = "概述\n" +
            "\n" +
            "    美丽的中世纪风格办公家具系列的一部分，包括文件柜、办公桌、书柜、会议桌等。\n" +
            "    多种外壳颜色和底座涂层可选。\n" +
            "    可选塑料前后靠背装饰（SWC-100）或10种面料和6种皮革的全面装饰（SWC-110）。\n" +
            "    底座涂层选项为：不锈钢、哑光黑色、光泽白色或铬。\n" +
            "    椅子可带或不带扶手。\n" +
            "    适用于家庭或商业场所。\n" +
            "    符合合同使用资格。\n" +
            "\n" +
            "结构\n" +
            "\n" +
            "    五个轮子的塑料涂层铝底座。\n" +
            "    气动椅子调节，方便升降。\n" +
            "\n" +
            "尺寸\n" +
            "\n" +
            "    宽度53厘米|20.87英寸\n" +
            "    深度51厘米|20.08英寸\n" +
            "    高度80厘米|31.50英寸\n" +
            "    座椅高度44厘米|17.32英寸\n" +
            "    座椅深度41厘米|16.14英寸\n" +
            "\n" +
            "选项\n" +
            "\n" +
            "    软地板或硬地板滚轮选项。\n" +
            "    两种座椅泡沫密度可选：中等（1.8磅/立方英尺）或高（2.8磅/立方英尺）。\n" +
            "    无扶手或8个位置PU扶手。\n" +
            "\n" +
            "材料\n" +
            "外壳底座滑动件\n" +
            "\n" +
            "    改性尼龙PA6/PA66涂层的铸铝。\n" +
            "    外壳厚度：10毫米。\n" +
            "    座椅\n" +
            "    HD36泡沫\n" +
            "\n" +
            "原产国\n" +
            "\n" +
            "    意大利";

    @Test
    public void iterative1() {

        String prompt = "您的任务是帮助营销团队基于技术说明书创建一个产品的营销描述。\n" +
                "\n" +
                "根据```标记的技术说明书中提供的信息，编写一个产品描述。\n" +
                "\n" +
                "技术说明: ```" + this.sheet + "```";

        String message = this.getCompletion(prompt);

        log.info("iterative1:\n{}", message);
    }

    @Test
    public void iterative2() {

        String prompt = "您的任务是帮助营销团队基于技术说明书创建一个产品的营销描述。\n" +
                "\n" +
                "根据```标记的技术说明书中提供的信息，编写一个产品描述。\n" +
                "使用最多50个词。\n" +
                "技术说明: ```" + this.sheet + "```";

        String message = this.getCompletion(prompt);

        log.info("iterative2:\n{}", message);
    }

    @Test
    public void iterative3() {

        String prompt = "您的任务是帮助营销团队基于技术说明书创建一个产品的零售网站描述。\n" +
                "根据```标记的技术说明书中提供的信息，编写一个产品描述。\n" +
                "该描述面向家具零售商，因此应具有技术性质，并侧重于产品的材料构造。\n" +
                "使用最多50个单词。\n" +
                "技术说明: ```" + this.sheet + "```";

        String message = this.getCompletion(prompt);

        log.info("iterative3:\n{}", message);
    }


    @Test
    public void iterative4() {

        String prompt = "您的任务是帮助营销团队基于技术说明书创建一个产品的零售网站描述。\n" +
                "根据```标记的技术说明书中提供的信息，编写一个产品描述。\n" +
                "该描述面向家具零售商，因此应具有技术性质，并侧重于产品的材料构造。\n" +
                "在描述末尾，包括技术规格中每个7个字符的产品ID。\n" +
                "使用最多50个单词。\n" +
                "技术说明: ```" + this.sheet + "```";

        String message = this.getCompletion(prompt);

        log.info("iterative4:\n{}", message);
    }


    @Test
    public void iterative5() {

        String prompt = "您的任务是帮助营销团队基于技术说明书创建一个产品的零售网站描述。\n" +
                "根据```标记的技术说明书中提供的信息，编写一个产品描述。\n" +
                "该描述面向家具零售商，因此应具有技术性质，并侧重于产品的材料构造。\n" +
                "在描述末尾，包括技术规格中每个7个字符的产品ID。\n" +
                "在描述之后，包括一个表格，提供产品的尺寸。表格应该有两列。第一列包括尺寸的名称。第二列只包括英寸的测量值。\n" +
                "给表格命名为“产品尺寸”。\n" +
                "将所有内容格式化为可用于网站的HTML格式。将描述放在<div>元素中。\n" +
                "技术说明: ```" + this.sheet + "```";

        String message = this.getCompletion(prompt);

        log.info("iterative5:\n{}", message);
    }


}
