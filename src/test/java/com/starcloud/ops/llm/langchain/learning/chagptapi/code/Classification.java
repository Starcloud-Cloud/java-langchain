package com.starcloud.ops.llm.langchain.learning.chagptapi.code;

import com.starcloud.ops.llm.langchain.learning.BaseTests;
import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Classification extends BaseTests {


    private String delimiter = "###";


    private String system = "你将获得客户服务查询。\n" +
            "每个客户服务查询都将用{delimiter}字符分隔。\n" +
            "将每个查询分类到一个主要类别和一个次要类别中。\n" +
            "以 JSON 格式提供你的输出，包含以下键：primary 和 secondary。\n" +
            "\n" +
            "主要类别：计费（Billing）、技术支持（Technical Support）、账户管理（Account Management）或一般咨询（General Inquiry）。\n" +
            "\n" +
            "计费次要类别：\n" +
            "取消订阅或升级（Unsubscribe or upgrade）\n" +
            "添加付款方式（Add a payment method）\n" +
            "收费解释（Explanation for charge）\n" +
            "争议费用（Dispute a charge）\n" +
            "\n" +
            "技术支持次要类别：\n" +
            "常规故障排除（General troubleshooting）\n" +
            "设备兼容性（Device compatibility）\n" +
            "软件更新（Software updates）\n" +
            "\n" +
            "账户管理次要类别：\n" +
            "重置密码（Password reset）\n" +
            "更新个人信息（Update personal information）\n" +
            "关闭账户（Close account）\n" +
            "账户安全（Account security）\n" +
            "\n" +
            "一般咨询次要类别：\n" +
            "产品信息（Product information）\n" +
            "定价（Pricing）\n" +
            "反馈（Feedback）\n" +
            "与人工对话（Speak to a human）";

    @Test
    public void test1() {

        String message = "我希望你删除我的个人资料和所有用户数据。";

        List<ChatMessage> chatMessages = new ArrayList<>();

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRole("system");
        chatMessage.setContent(system);
        chatMessages.add(chatMessage);

        ChatMessage chatMessage2 = new ChatMessage();
        chatMessage2.setRole("user");
        chatMessage2.setContent(delimiter + message + delimiter);
        chatMessages.add(chatMessage2);

        String result = this.getCompletionFromMessage(chatMessages, 1);

        log.info("test1:\n{}", result);
    }

    @Test
    public void test2() {

        String message = "告诉我更多有关你们的平板电脑的信息";

        List<ChatMessage> chatMessages = new ArrayList<>();

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRole("system");
        chatMessage.setContent(system);
        chatMessages.add(chatMessage);

        ChatMessage chatMessage2 = new ChatMessage();
        chatMessage2.setRole("user");
        chatMessage2.setContent(delimiter + message + delimiter);
        chatMessages.add(chatMessage2);

        String result = this.getCompletionFromMessage(chatMessages, 1);

        log.info("test2:\n{}", result);
    }


}
