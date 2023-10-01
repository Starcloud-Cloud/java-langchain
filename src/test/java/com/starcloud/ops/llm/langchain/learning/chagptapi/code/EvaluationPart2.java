package com.starcloud.ops.llm.langchain.learning.chagptapi.code;


import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.starcloud.ops.llm.langchain.learning.BaseTests;
import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class EvaluationPart2 extends BaseTests {

    @Test
    public void test1() {

        String delimiter = "###";

        String customer = "告诉我有关 the smartx pro phone 和 the fotosnap camera, the dslr one 的信息。\n" +
                "另外，你们这有什么 TVs ？";

        //从问题中抽取商品大类和名称
        String result = this.getProductsFromQuery(customer);

        JSONArray jsonArray = JSONUtil.parseArray(result);

        //查找商品对应信息
        List<JSONObject> products = this.getMentionedProductInfo(jsonArray);


        List<ChatMessage> messages = new ArrayList<>();

        String system = "您是一家大型电子商店的客户服务助理。\n" +
                "请用友好和乐于助人的口吻回答问题，提供简洁明了的答案。\n" +
                "确保向用户提出相关的后续问题。";


        ChatMessage systemMessage = new ChatMessage();
        systemMessage.setRole("system");
        systemMessage.setContent(system);
        messages.add(systemMessage);

        ChatMessage userMessage = new ChatMessage();
        userMessage.setRole("user");
        userMessage.setContent(delimiter + customer + delimiter);
        messages.add(userMessage);

        ChatMessage assistantMessage = new ChatMessage();
        assistantMessage.setRole("assistant");
        assistantMessage.setContent("相关产品信息:\n" + JSONUtil.toJsonStr(products));
        messages.add(assistantMessage);

        String answer = this.getCompletionFromMessage(messages, 0);

        log.info("test1:\n{}", answer);
    }


    @Test
    public void test2() {

        String delimiter = "###";

        String customer = "告诉我有关 the smartx pro phone 和 the fotosnap camera, the dslr one 的信息。\n" +
                "另外，你们这有什么 TVs ？";

        //从问题中抽取商品大类和名称
        String result = this.getProductsFromQuery(customer);

        JSONArray jsonArray = JSONUtil.parseArray(result);

        //查找商品对应信息
        List<JSONObject> products = this.getMentionedProductInfo(jsonArray);

        log.info("products: {}", products);

        List<ChatMessage> messages = new ArrayList<>();

        String system = "您是一家大型电子商店的客户服务助理。\n" +
                "请用友好和乐于助人的口吻回答问题，提供简洁明了的答案。\n" +
                "确保向用户提出相关的后续问题。";


        ChatMessage systemMessage = new ChatMessage();
        systemMessage.setRole("system");
        systemMessage.setContent(system);
        messages.add(systemMessage);

        ChatMessage userMessage = new ChatMessage();
        userMessage.setRole("user");
        userMessage.setContent(delimiter + customer + delimiter);
        messages.add(userMessage);

        ChatMessage assistantMessage = new ChatMessage();
        assistantMessage.setRole("assistant");
        assistantMessage.setContent("相关产品信息:\n" + JSONUtil.toJsonStr(products));
        messages.add(assistantMessage);

        String answer = this.getCompletionFromMessage(messages, 0);

        log.info("test1:\n{}", answer);
    }


    @Test
    public void test3() {

        String customer = "告诉我有关 the smartx pro phone 和 the fotosnap camera, the dslr one 的信息。\n" +
                "另外，你们这有什么 TVs ？";

        String products = "[{\"name\":\"SmartX ProPhone\",\"category\":\"智能手机和配件\",\"brand\":\"SmartX\",\"model_number\":\"SX-PP10\",\"warranty\":\"1 year\",\"rating\":4.6,\"features\":[\"6.1-inch display\",\"128GB storage\",\"12MP dual camera\",\"5G\"],\"description\":\"一款拥有先进摄像功能的强大智能手机。\",\"price\":899.99}, {\"name\":\"FotoSnap DSLR Camera\",\"category\":\"相机和摄像机\",\"brand\":\"FotoSnap\",\"model_number\":\"FS-DSLR200\",\"warranty\":\"1 year\",\"rating\":4.7,\"features\":[\"24.2MP sensor\",\"1080p video\",\"3-inch LCD\",\"Interchangeable lenses\"],\"description\":\"使用这款多功能的单反相机，捕捉惊艳的照片和视频。\",\"price\":599.99}, {\"name\":\"CineView 4K TV\",\"category\":\"电视和家庭影院系统\",\"brand\":\"CineView\",\"model_number\":\"CV-4K55\",\"warranty\":\"2 years\",\"rating\":4.8,\"features\":[\"55-inch display\",\"4K resolution\",\"HDR\",\"Smart TV\"],\"description\":\"一款色彩鲜艳、智能功能丰富的惊艳4K电视。\",\"price\":599.99}, {\"name\":\"CineView 8K TV\",\"category\":\"电视和家庭影院系统\",\"brand\":\"CineView\",\"model_number\":\"CV-8K65\",\"warranty\":\"2 years\",\"rating\":4.9,\"features\":[\"65-inch display\",\"8K resolution\",\"HDR\",\"Smart TV\"],\"description\":\"通过这款惊艳的8K电视，体验未来。\",\"price\":2999.99}, {\"name\":\"CineView OLED TV\",\"category\":\"电视和家庭影院系统\",\"brand\":\"CineView\",\"model_number\":\"CV-OLED55\",\"warranty\":\"2 years\",\"rating\":4.7,\"features\":[\"55-inch display\",\"4K resolution\",\"HDR\",\"Smart TV\"],\"description\":\"通过这款OLED电视，体验真正的五彩斑斓。\",\"price\":1499.99}]";

        String assistantAnswer = "我们有以下产品可供您选择：\n" +
                "\n" +
                "1. SmartX ProPhone - 这是一款功能强大的智能手机，拥有6.1英寸的显示屏，128GB的存储空间，12MP的双摄像头和5G网络支持。它的价格是899.99美元。\n" +
                "\n" +
                "2. FotoSnap DSLR Camera - 这是一款多功能的单反相机，拥有24.2MP的传感器，1080p的视频拍摄能力，3英寸的LCD屏幕和可更换镜头。它的价格是599.99美元。\n" +
                "\n" +
                "关于电视，我们有以下几款可供选择：\n" +
                "\n" +
                "1. CineView 4K TV - 这是一款55英寸的4K电视，支持HDR和智能电视功能。它的价格是599.99美元。\n" +
                "\n" +
                "2. CineView 8K TV - 这是一款65英寸的8K电视，支持HDR和智能电视功能。它的价格是2999.99美元。\n" +
                "\n" +
                "3. CineView OLED TV - 这是一款55英寸的OLED电视，支持4K分辨率、HDR和智能电视功能。它的价格是1499.99美元。\n" +
                "\n" +
                "请问您对以上产品有什么进一步的问题或者需要了解的信息吗？";

        String result = this.evalWithRubric(customer, products, assistantAnswer);

        log.info("test3: \n{}", result);
    }


    @Test
    public void test4() {

        String customer = "告诉我有关 the smartx pro phone 和 the fotosnap camera, the dslr one 的信息。\\n另外，你们这有什么 TVs ？";

        String idealAnswer = "SmartX ProPhone是一款功能强大的智能手机，具有6.1英寸的显示屏，128GB的存储空间，12MP双摄像头和5G网络。它的价格为899.99美元，保修期为1年。\n" +
                "FotoSnap DSLR相机是一款功能强大的相机，具有24.2MP传感器，1080p视频，3英寸LCD屏幕和可更换镜头。它的价格为599.99美元，保修期为1年。\n" +
                "我们有多种电视可供选择，包括CineView 4K电视，CineView 8K电视和CineView OLED电视。CineView 4K电视具有55英寸的显示屏，4K分辨率，HDR和智能电视功能，价格为599.99美元，保修期为2年。CineView 8K电视具有65英寸的显示屏，8K分辨率，HDR和智能电视功能，价格为2999.99美元，保修期为2年。CineView OLED电视具有55英寸的显示屏，4K分辨率，HDR和智能电视功能，价格为1499.99美元，保修期为2年。";

        String assistantAnswer = "我们有以下产品可供您选择：\n" +
                "\n" +
                "1. SmartX ProPhone - 这是一款功能强大的智能手机，拥有6.1英寸的显示屏，128GB的存储空间，12MP的双摄像头和5G网络支持。它的价格是899.99美元。\n" +
                "\n" +
                "2. FotoSnap DSLR Camera - 这是一款多功能的单反相机，拥有24.2MP的传感器，1080p的视频拍摄能力，3英寸的LCD屏幕和可更换镜头。它的价格是599.99美元。\n" +
                "\n" +
                "关于电视，我们有以下几款可供选择：\n" +
                "\n" +
                "1. CineView 4K TV - 这是一款55英寸的4K电视，支持HDR和智能电视功能。它的价格是599.99美元。\n" +
                "\n" +
                "2. CineView 8K TV - 这是一款65英寸的8K电视，支持HDR和智能电视功能。它的价格是2999.99美元。\n" +
                "\n" +
                "3. CineView OLED TV - 这是一款55英寸的OLED电视，支持4K分辨率、HDR和智能电视功能。它的价格是1499.99美元。\n" +
                "\n" +
                "请问您对以上产品有什么进一步的问题或者需要了解的信息吗？";

        String result = this.evalVsIdeal(customer, idealAnswer, assistantAnswer);

        log.info("test4: \n{}", result);
    }

    /**
     * 使用 GPT API 评估生成的回答
     *
     * @param customerMsg     用户的输入
     * @param context         回答需要的上下文内容
     * @param assistantAnswer GPT的回答
     */
    public String evalWithRubric(String customerMsg, String context, String assistantAnswer) {

        String system = "你是一位助理，通过查看客户服务代理使用的上下文来评估客户服务代理回答用户问题的情况。";
        String user = "你正在根据代理使用的上下文评估对问题的提交答案。以下是数据：\n" +
                "    [开始]\n" +
                "    ************\n" +
                "    [用户问题]: " + customerMsg + "\n" +
                "    ************\n" +
                "    [使用的上下文]: " + context + "\n" +
                "    ************\n" +
                "    [客户代理的回答]: " + assistantAnswer + "\n" +
                "    ************\n" +
                "    [结束]\n" +
                "\n" +
                "    请将提交的答案的事实内容与上下文进行比较，忽略样式、语法或标点符号上的差异。\n" +
                "    回答以下问题：\n" +
                "    助手的回应是否只基于所提供的上下文？（是或否）\n" +
                "    回答中是否包含上下文中未提供的信息？（是或否）\n" +
                "    回应与上下文之间是否存在任何不一致之处？（是或否）\n" +
                "    计算用户提出了多少个问题。（输出一个数字）\n" +
                "    对于用户提出的每个问题，是否有相应的回答？\n" +
                "    问题1：（是或否）\n" +
                "    问题2：（是或否）\n" +
                "    ...\n" +
                "    问题N：（是或否）\n" +
                "    在提出的问题数量中，有多少个问题在回答中得到了回应？（输出一个数字）";


        List<ChatMessage> messages = new ArrayList<>();

        ChatMessage systemMessage = new ChatMessage();
        systemMessage.setRole("system");
        systemMessage.setContent(system);
        messages.add(systemMessage);

        ChatMessage userMessage = new ChatMessage();
        userMessage.setRole("user");
        userMessage.setContent(user);
        messages.add(userMessage);

        String answer = this.getCompletionFromMessage(messages, 0);

        return answer;
    }


    public String evalVsIdeal(String customerMsg, String idealAnswer, String assistantAnswer) {

        String system = "您是一位助理，通过将客户服务代理的回答与理想（专家）回答进行比较，评估客户服务代理对用户问题的回答质量。\n" +
                "请输出一个单独的字母（A 、B、C、D、E），不要包含其他内容。";

        String user = "您正在比较一个给定问题的提交答案和专家答案。数据如下:\n" +
                "    [开始]\n" +
                "    ************\n" +
                "    [问题]: " + customerMsg + "\n" +
                "    ************\n" +
                "    [专家答案]: " + idealAnswer + "\n" +
                "    ************\n" +
                "    [提交答案]: " + assistantAnswer + "\n" +
                "    ************\n" +
                "    [结束]\n" +
                "\n" +
                "    比较提交答案的事实内容与专家答案。忽略样式、语法或标点符号上的差异。\n" +
                "    提交的答案可能是专家答案的子集、超集，或者与之冲突。确定适用的情况，并通过选择以下选项之一回答问题：\n" +
                "    （A）提交的答案是专家答案的子集，并且与之完全一致。\n" +
                "    （B）提交的答案是专家答案的超集，并且与之完全一致。\n" +
                "    （C）提交的答案包含与专家答案完全相同的细节。\n" +
                "    （D）提交的答案与专家答案存在分歧。\n" +
                "    （E）答案存在差异，但从事实的角度来看这些差异并不重要。\n" +
                "    选项：ABCDE";


        List<ChatMessage> messages = new ArrayList<>();

        ChatMessage systemMessage = new ChatMessage();
        systemMessage.setRole("system");
        systemMessage.setContent(system);
        messages.add(systemMessage);

        ChatMessage userMessage = new ChatMessage();
        userMessage.setRole("user");
        userMessage.setContent(user);
        messages.add(userMessage);

        String answer = this.getCompletionFromMessage(messages, 0);

        return answer;
    }


    public List<JSONObject> getMentionedProductInfo(JSONArray productsList) {

        Pcp pcp = new Pcp();

        List<JSONObject> productInfo = new ArrayList<>();

        for (Object o : productsList) {
            JSONObject jsonObject = (JSONObject) o;

            String category = jsonObject.getStr("category");
            List<String> products = jsonObject.getBeanList("products", String.class);

            for (String product : products) {
                productInfo.add(pcp.getProductByName(product));
            }
            productInfo.addAll(pcp.getProductsByCategory(category));
        }

        return productInfo;

    }

}
