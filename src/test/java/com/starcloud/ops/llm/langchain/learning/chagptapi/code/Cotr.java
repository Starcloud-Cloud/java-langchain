package com.starcloud.ops.llm.langchain.learning.chagptapi.code;

import com.starcloud.ops.llm.langchain.learning.BaseTests;
import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class Cotr extends BaseTests {

    private String delimiter = "###";

    private String system = "请按照以下步骤回答客户的查询。客户的查询将以四个井号（#）分隔，即 " + delimiter + "。\n" +
            "\n" +
            "步骤 1:" + delimiter + " 首先确定用户是否正在询问有关特定产品或产品的问题。产品类别不计入范围。\n" +
            "\n" +
            "步骤 2:" + delimiter + " 如果用户询问特定产品，请确认产品是否在以下列表中。所有可用产品：\n" +
            "\n" +
            "产品：TechPro 超极本\n" +
            "类别：计算机和笔记本电脑\n" +
            "品牌：TechPro\n" +
            "型号：TP-UB100\n" +
            "保修期：1 年\n" +
            "评分：4.5\n" +
            "特点：13.3 英寸显示屏，8GB RAM，256GB SSD，Intel Core i5 处理器\n" +
            "描述：一款适用于日常使用的时尚轻便的超极本。\n" +
            "价格：$799.99\n" +
            "\n" +
            "产品：BlueWave 游戏笔记本电脑\n" +
            "类别：计算机和笔记本电脑\n" +
            "品牌：BlueWave\n" +
            "型号：BW-GL200\n" +
            "保修期：2 年\n" +
            "评分：4.7\n" +
            "特点：15.6 英寸显示屏，16GB RAM，512GB SSD，NVIDIA GeForce RTX 3060\n" +
            "描述：一款高性能的游戏笔记本电脑，提供沉浸式体验。\n" +
            "价格：$1199.99\n" +
            "\n" +
            "产品：PowerLite 可转换笔记本电脑\n" +
            "类别：计算机和笔记本电脑\n" +
            "品牌：PowerLite\n" +
            "型号：PL-CV300\n" +
            "保修期：1年\n" +
            "评分：4.3\n" +
            "特点：14 英寸触摸屏，8GB RAM，256GB SSD，360 度铰链\n" +
            "描述：一款多功能可转换笔记本电脑，具有响应触摸屏。\n" +
            "价格：$699.99\n" +
            "\n" +
            "产品：TechPro 台式电脑\n" +
            "类别：计算机和笔记本电脑\n" +
            "品牌：TechPro\n" +
            "型号：TP-DT500\n" +
            "保修期：1年\n" +
            "评分：4.4\n" +
            "特点：Intel Core i7 处理器，16GB RAM，1TB HDD，NVIDIA GeForce GTX 1660\n" +
            "描述：一款功能强大的台式电脑，适用于工作和娱乐。\n" +
            "价格：$999.99\n" +
            "\n" +
            "产品：BlueWave Chromebook\n" +
            "类别：计算机和笔记本电脑\n" +
            "品牌：BlueWave\n" +
            "型号：BW-CB100\n" +
            "保修期：1 年\n" +
            "评分：4.1\n" +
            "特点：11.6 英寸显示屏，4GB RAM，32GB eMMC，Chrome OS\n" +
            "描述：一款紧凑而价格实惠的 Chromebook，适用于日常任务。\n" +
            "价格：$249.99\n" +
            "\n" +
            "步骤 3:" + delimiter + " 如果消息中包含上述列表中的产品，请列出用户在消息中做出的任何假设，例如笔记本电脑 X 比笔记本电脑 Y 大，或者笔记本电脑 Z 有 2 年保修期。\n" +
            "\n" +
            "步骤 4:" + delimiter + " 如果用户做出了任何假设，请根据产品信息确定假设是否正确。\n" +
            "\n" +
            "步骤 5:" + delimiter + " 如果用户有任何错误的假设，请先礼貌地纠正客户的错误假设（如果适用）。只提及或引用可用产品列表中的产品，因为这是商店销售的唯一五款产品。以友好的口吻回答客户。\n" +
            "\n" +
            "使用以下格式回答问题：\n" +
            "步骤 1:" + delimiter + " <步骤 1的推理>\n" +
            "步骤 2:" + delimiter + " <步骤 2 的推理>\n" +
            "步骤 3:" + delimiter + " <步骤 3 的推理>\n" +
            "步骤 4:" + delimiter + " <步骤 4 的推理>\n" +
            "回复客户:{delimiter} <回复客户的内容>\n" +
            "\n" +
            "请确保在每个步骤之间使用 {" + delimiter + "} 进行分隔。";

    @Test
    public void test1() {

        String user = delimiter + "BlueWave Chromebook 比 TechPro 台式电脑贵多少？" + delimiter;

        List<ChatMessage> messages = new ArrayList<>();

        ChatMessage systemMessage = new ChatMessage();
        systemMessage.setRole("system");
        systemMessage.setContent(system);
        messages.add(systemMessage);

        ChatMessage userMessage = new ChatMessage();
        userMessage.setRole("user");
        userMessage.setContent(user);
        messages.add(userMessage);

        String message = this.getCompletionFromMessage(messages, 0);

        log.info("test1:\n{}", message);
    }


    @Test
    public void test2() {

        String user = delimiter + "你有电视机嘛？" + delimiter;

        List<ChatMessage> messages = new ArrayList<>();

        ChatMessage systemMessage = new ChatMessage();
        systemMessage.setRole("system");
        systemMessage.setContent(system);
        messages.add(systemMessage);

        ChatMessage userMessage = new ChatMessage();
        userMessage.setRole("user");
        userMessage.setContent(user);
        messages.add(userMessage);

        String message = this.getCompletionFromMessage(messages, 0);

        log.info("test2:\n{}", message);
    }


    @Test
    public void test3() {

        String user = delimiter + "你有电视机嘛？" + delimiter;

        List<ChatMessage> messages = new ArrayList<>();

        ChatMessage systemMessage = new ChatMessage();
        systemMessage.setRole("system");
        systemMessage.setContent(system);
        messages.add(systemMessage);

        ChatMessage userMessage = new ChatMessage();
        userMessage.setRole("user");
        userMessage.setContent(user);
        messages.add(userMessage);

        String message = this.getCompletionFromMessage(messages, 0);

        //取出最后一行回复内容
        String lastElement = Arrays.stream(message.split(delimiter))
                .reduce((first, second) -> second)
                .orElse(null);

        log.info("test3:\n{}", lastElement);
    }

}
