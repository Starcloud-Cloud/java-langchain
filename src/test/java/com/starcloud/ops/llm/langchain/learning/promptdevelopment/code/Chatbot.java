package com.starcloud.ops.llm.langchain.learning.promptdevelopment.code;

import com.starcloud.ops.llm.langchain.learning.BaseTests;
import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class Chatbot extends BaseTests {

    @Test
    public void iterative1() {
        List<ChatMessage> chatMessages = new ArrayList<>();

        ChatMessage message = new ChatMessage();
        message.setRole("system");
        message.setContent("你是一个像莎士比亚一样说话的助手。");
        chatMessages.add(message);

        ChatMessage message1 = new ChatMessage();
        message1.setRole("user");
        message1.setContent("给我讲个笑话");
        chatMessages.add(message1);

        ChatMessage message2 = new ChatMessage();
        message2.setRole("assistant");
        message2.setContent("鸡为什么过马路");
        chatMessages.add(message2);

        ChatMessage message3 = new ChatMessage();
        message3.setRole("user");
        message3.setContent("我不知道");
        chatMessages.add(message3);


        String result = this.getCompletionFromMessage(chatMessages, 1.5d);

        log.info("iterative1:\n{}", result);
    }


    @Test
    public void iterative2() {
        List<ChatMessage> chatMessages = new ArrayList<>();

        ChatMessage message = new ChatMessage();
        message.setRole("system");
        message.setContent("你是个友好的聊天机器人。");
        chatMessages.add(message);

        ChatMessage message1 = new ChatMessage();
        message1.setRole("user");
        message1.setContent("Hi, 我是Isa。");
        chatMessages.add(message1);


        String result = this.getCompletionFromMessage(chatMessages, 1.5d);

        log.info("iterative2:\n{}", result);
    }

    @Test
    public void iterative3() {
        List<ChatMessage> chatMessages = new ArrayList<>();

        ChatMessage message = new ChatMessage();
        message.setRole("system");
        message.setContent("你是个友好的聊天机器人。");
        chatMessages.add(message);

        ChatMessage message1 = new ChatMessage();
        message1.setRole("user");
        message1.setContent("好，你能提醒我，我的名字是什么吗？");
        chatMessages.add(message1);


        String result = this.getCompletionFromMessage(chatMessages, 1.5d);

        log.info("iterative3:\n{}", result);
    }


    @Test
    public void iterative4() {
        List<ChatMessage> chatMessages = new ArrayList<>();

        ChatMessage message = new ChatMessage();
        message.setRole("system");
        message.setContent("你是个友好的聊天机器人。");
        chatMessages.add(message);

        ChatMessage message1 = new ChatMessage();
        message1.setRole("user");
        message1.setContent("Hi, 我是Isa");
        chatMessages.add(message1);

        ChatMessage message2 = new ChatMessage();
        message2.setRole("assistant");
        message2.setContent("Hi Isa! 很高兴认识你。今天有什么可以帮到你的吗?");
        chatMessages.add(message2);

        ChatMessage message3 = new ChatMessage();
        message3.setRole("user");
        message3.setContent("是的，你可以提醒我, 我的名字是什么?");
        chatMessages.add(message3);


        String result = this.getCompletionFromMessage(chatMessages, 1.5d);

        log.info("iterative4:\n{}", result);
    }


    private List<ChatMessage> history = new ArrayList<>();

    @Test
    public void iterative5() {

        ChatMessage message = new ChatMessage();
        message.setRole("system");
        message.setContent("你是订餐机器人，为披萨餐厅自动收集订单信息。\n" +
                "你要首先问候顾客。然后等待用户回复收集订单信息。收集完信息需确认顾客是否还需要添加其他内容。\n" +
                "最后需要询问是否自取或外送，如果是外送，你要询问地址。\n" +
                "最后告诉顾客订单总金额，并送上祝福。\n" +
                "\n" +
                "请确保明确所有选项、附加项和尺寸，以便从菜单中识别出该项唯一的内容。\n" +
                "你的回应应该以简短、非常随意和友好的风格呈现。\n" +
                "\n" +
                "菜单包括：\n" +
                "\n" +
                "菜品：\n" +
                "意式辣香肠披萨（大、中、小） 12.95、10.00、7.00\n" +
                "芝士披萨（大、中、小） 10.95、9.25、6.50\n" +
                "茄子披萨（大、中、小） 11.95、9.75、6.75\n" +
                "薯条（大、小） 4.50、3.50\n" +
                "希腊沙拉 7.25\n" +
                "\n" +
                "配料：\n" +
                "奶酪 2.00\n" +
                "蘑菇 1.50\n" +
                "香肠 3.00\n" +
                "加拿大熏肉 3.50\n" +
                "AI酱 1.50\n" +
                "辣椒 1.00\n" +
                "\n" +
                "饮料：\n" +
                "可乐（大、中、小） 3.00、2.00、1.00\n" +
                "雪碧（大、中、小） 3.00、2.00、1.00\n" +
                "瓶装水 5.00");
        history.add(message);


        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRole("user");
        chatMessage.setContent(line);
        history.add(chatMessage);

        this.collectMessages(history, 1);

    }


    public void collectMessages(List<ChatMessage> chatMessages, int index) {

        String result = this.getCompletionFromMessage(chatMessages, 1.5d);

        ChatMessage message = new ChatMessage();
        message.setRole("assistant");
        message.setContent(result);

        history.add(message);

        log.info("assistant: {}", message);


        if (index >= 5) {

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setRole("system");
            chatMessage.setContent("创建上一个食品订单的 json 摘要。\n" +
                    "逐项列出每件商品的价格，字段应该是 1) 披萨，包括大小 2) 配料列表 3) 饮料列表，包括大小 4) 配菜列表包括大小 5) 总价");
            history.add(chatMessage);

            String anw = this.getCompletionFromMessage(history, 1.5d);

            log.info("collectMessages end: {}", anw);

            return;
        }


        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRole("user");
        chatMessage.setContent(line);
        history.add(chatMessage);

        index++;

        collectMessages(history, index);

    }
}
