package com.starcloud.ops.llm.langchain.learning.chagptapi.code;

import com.starcloud.ops.llm.langchain.learning.BaseTests;
import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Tlm extends BaseTests {


    @Test
    public void test1() {

        String prompt = "中国的首都是哪里？";

        String message = this.getCompletion(prompt);

        log.info("test1:\n{}", message);
    }


    @Test
    public void test2() {

        String prompt = "Take the letters in lollipop \n" +
                "and reverse them";

        String message = this.getCompletion(prompt);

        //The reversed letters of "lollipop" are "pillipol".
        //实际上 lollipop 的 翻转是 popillol
        log.info("test2:\n{}", message);


        String prompt1 = "Take the letters in\n" +
                "l-o-l-l-i-p-o-p and reverse them";

        String message1 = this.getCompletion(prompt1);

        //p-o-p-i-l-l-o-l
        //这次正确了
        log.info("test2:\n{}", message1);
    }


    @Test
    public void test3() {


        List<ChatMessage> chatMessages = new ArrayList<>();

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRole("system");
        chatMessage.setContent("你是一个助理， 并以 Seuss 苏斯博士的风格作出回答。");
        chatMessages.add(chatMessage);

        ChatMessage chatMessage2 = new ChatMessage();
        chatMessage2.setRole("user");
        chatMessage2.setContent("就快乐的小鲸鱼为主题给我写一首短诗");
        chatMessages.add(chatMessage2);

        String message1 = this.getCompletionFromMessage(chatMessages, 1);

        log.info("test3:\n{}", message1);
    }


    @Test
    public void test4() {


        List<ChatMessage> chatMessages = new ArrayList<>();

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRole("system");

        //长度控制
        chatMessage.setContent("你的所有答复只能是一句话");
        chatMessages.add(chatMessage);

        ChatMessage chatMessage2 = new ChatMessage();
        chatMessage2.setRole("user");
        chatMessage2.setContent("写一个关于快乐的小鲸鱼的故事");
        chatMessages.add(chatMessage2);

        String message1 = this.getCompletionFromMessage(chatMessages, 1);

        log.info("test4:\n{}", message1);
    }


    @Test
    public void test5() {


        List<ChatMessage> chatMessages = new ArrayList<>();

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRole("system");

        //结合
        chatMessage.setContent("你是一个助理， 并以 Seuss 苏斯博士的风格作出回答，只回答一句话");
        chatMessages.add(chatMessage);

        ChatMessage chatMessage2 = new ChatMessage();
        chatMessage2.setRole("user");
        chatMessage2.setContent("写一个关于快乐的小鲸鱼的故事");
        chatMessages.add(chatMessage2);

        String message1 = this.getCompletionFromMessage(chatMessages, 1);

        log.info("test5:\n{}", message1);
    }


}
