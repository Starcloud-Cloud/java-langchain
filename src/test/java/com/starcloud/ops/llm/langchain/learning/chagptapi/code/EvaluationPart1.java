package com.starcloud.ops.llm.langchain.learning.chagptapi.code;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.starcloud.ops.llm.langchain.learning.BaseTests;
import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.*;

@Slf4j
public class EvaluationPart1 extends BaseTests {

    String delimiter = "###";

    private String allProducts = "{\"Computers and Laptops\":[\"TechPro Ultrabook\",\"BlueWave Gaming Laptop\",\"PowerLite Convertible\",\"TechPro Desktop\",\"BlueWave Chromebook\"],\"Smartphones and Accessories\":[\"SmartX ProPhone\",\"MobiTech PowerCase\",\"SmartX MiniPhone\",\"MobiTech Wireless Charger\",\"SmartX EarBuds\"],\"Televisions and Home Theater Systems\":[\"CineView 4K TV\",\"SoundMax Home Theater\",\"CineView 8K TV\",\"SoundMax Soundbar\",\"CineView OLED TV\"],\"Gaming Consoles and Accessories\":[\"GameSphere X\",\"ProGamer Controller\",\"GameSphere Y\",\"ProGamer Racing Wheel\",\"GameSphere VR Headset\"],\"Audio Equipment\":[\"AudioPhonic Noise-Canceling Headphones\",\"WaveSound Bluetooth Speaker\",\"AudioPhonic True Wireless Earbuds\",\"WaveSound Soundbar\",\"AudioPhonic Turntable\"],\"Cameras and Camcorders\":[\"FotoSnap DSLR Camera\",\"ActionCam 4K\",\"FotoSnap Mirrorless Camera\",\"ZoomMaster Camcorder\",\"FotoSnap Instant Camera\"]}";


    @Test
    public void test1() {

        //第一个评估
        String result = findCategoryAndProductV1("如果我预算有限，我可以买哪款电视？", allProducts);

        log.info("test1: {}", result);
    }

    @Test
    public void test2() {

        //第二个评估
        String result = findCategoryAndProductV1("我需要一个智能手机的充电器", allProducts);

        log.info("test2: {}", result);
    }

    @Test
    public void test3() {

        //第三个评估
        String result = findCategoryAndProductV1("你们有哪些电脑?", allProducts);

        log.info("test3: {}", result);
    }


    @Test
    public void test4() {

        //第四个评估
        String result = findCategoryAndProductV1(" 告诉我关于smartx pro手机和fotosnap相机的信息，那款DSLR的。\n" +
                "另外，你们有哪些电视？", allProducts);

        log.info("test4: {}", result);
    }

    @Test
    public void test5() {

        //第五个评估
        String result = findCategoryAndProductV1("告诉我关于CineView电视的信息，那款8K的，还有Gamesphere游戏机，X款的。\n" +
                "我预算有限，你们有哪些电脑？", allProducts);

        log.info("test5: {}", result);
    }

    @Test
    public void test6() {

        String result = findCategoryAndProductV2("告诉我关于smartx pro手机和fotosnap相机的信息，那款DSLR的。\n" +
                "另外，你们有哪些电视？", allProducts);

        log.info("test6: {}", result);
    }


    @Test
    public void test7() {

        String result = findCategoryAndProductV2("如果我预算有限，我可以买哪款电视？", allProducts);

        log.info("test7: {}", result);
    }


    @Test
    public void test8() {


        //测试用例
        String testCase = "[{\"customer_msg\":\"Which TV can I buy if I'm on a budget?\",\"ideal_answer\":{\"Televisions and Home Theater Systems\":[\"CineView 4K TV\",\"SoundMax Home Theater\",\"CineView 8K TV\",\"SoundMax Soundbar\",\"CineView OLED TV\"]}},{\"customer_msg\":\"I need a charger for my smartphone\",\"ideal_answer\":{\"Smartphones and Accessories\":[\"MobiTech PowerCase\",\"MobiTech Wireless Charger\",\"SmartX EarBuds\"]}},{\"customer_msg\":\"What computers do you have?\",\"ideal_answer\":{\"Computers and Laptops\":[\"TechPro Ultrabook\",\"BlueWave Gaming Laptop\",\"PowerLite Convertible\",\"TechPro Desktop\",\"BlueWave Chromebook\"]}},{\"customer_msg\":\"tell me about the smartx pro phone and the fotosnap camera, the dslr one. Also, what TVs do you have?\",\"ideal_answer\":{\"Smartphones and Accessories\":[\"SmartX ProPhone\"],\"Cameras and Camcorders\":[\"FotoSnap DSLR Camera\"],\"Televisions and Home Theater Systems\":[\"CineView 4K TV\",\"SoundMax Home Theater\",\"CineView 8K TV\",\"SoundMax Soundbar\",\"CineView OLED TV\"]}},{\"customer_msg\":\"tell me about the CineView TV, the 8K one, Gamesphere console, the X one.\\nI'm on a budget, what computers do you have?\",\"ideal_answer\":{\"Televisions and Home Theater Systems\":[\"CineView 8K TV\"],\"Gaming Consoles and Accessories\":[\"GameSphere X\"],\"Computers and Laptops\":[\"TechPro Ultrabook\",\"BlueWave Gaming Laptop\",\"PowerLite Convertible\",\"TechPro Desktop\",\"BlueWave Chromebook\"]}},{\"customer_msg\":\"What smartphones do you have?\",\"ideal_answer\":{\"Smartphones and Accessories\":[\"SmartX ProPhone\",\"MobiTech PowerCase\",\"SmartX MiniPhone\",\"MobiTech Wireless Charger\",\"SmartX EarBuds\"]}},{\"customer_msg\":\"I'm on a budget. Can you recommend some smartphones to me?\",\"ideal_answer\":{\"Smartphones and Accessories\":[\"SmartX EarBuds\",\"SmartX MiniPhone\",\"MobiTech PowerCase\",\"SmartX ProPhone\",\"MobiTech Wireless Charger\"]}},{\"customer_msg\":\"What Gaming consoles would be good for my friend who is into racing games?\",\"ideal_answer\":{\"Gaming Consoles and Accessories\":[\"GameSphere X\",\"ProGamer Controller\",\"GameSphere Y\",\"ProGamer Racing Wheel\",\"GameSphere VR Headset\"]}},{\"customer_msg\":\"What could be a good present for my videographer friend?\",\"ideal_answer\":{\"Cameras and Camcorders\":[\"FotoSnap DSLR Camera\",\"ActionCam 4K\",\"FotoSnap Mirrorless Camera\",\"ZoomMaster Camcorder\",\"FotoSnap Instant Camera\"]}},{\"customer_msg\":\"I would like a hot tub time machine.\",\"ideal_answer\":[]}]";
        JSONArray array = JSONUtil.parseArray(testCase);

        JSONObject cc = (JSONObject) array.get(7);
        String customerMsg = cc.getStr("customer_msg");
        Object idealAnswer = cc.getJSONObject("ideal_answer");

        log.info("用户提问: {}", customerMsg);
        log.info("标准答案: {}", idealAnswer);

    }


    @Test
    public void test9() {

        //测试用例
        String testCase = "[{\"customer_msg\":\"Which TV can I buy if I'm on a budget?\",\"ideal_answer\":{\"Televisions and Home Theater Systems\":[\"CineView 4K TV\",\"SoundMax Home Theater\",\"CineView 8K TV\",\"SoundMax Soundbar\",\"CineView OLED TV\"]}},{\"customer_msg\":\"I need a charger for my smartphone\",\"ideal_answer\":{\"Smartphones and Accessories\":[\"MobiTech PowerCase\",\"MobiTech Wireless Charger\",\"SmartX EarBuds\"]}},{\"customer_msg\":\"What computers do you have?\",\"ideal_answer\":{\"Computers and Laptops\":[\"TechPro Ultrabook\",\"BlueWave Gaming Laptop\",\"PowerLite Convertible\",\"TechPro Desktop\",\"BlueWave Chromebook\"]}},{\"customer_msg\":\"tell me about the smartx pro phone and the fotosnap camera, the dslr one. Also, what TVs do you have?\",\"ideal_answer\":{\"Smartphones and Accessories\":[\"SmartX ProPhone\"],\"Cameras and Camcorders\":[\"FotoSnap DSLR Camera\"],\"Televisions and Home Theater Systems\":[\"CineView 4K TV\",\"SoundMax Home Theater\",\"CineView 8K TV\",\"SoundMax Soundbar\",\"CineView OLED TV\"]}},{\"customer_msg\":\"tell me about the CineView TV, the 8K one, Gamesphere console, the X one.\\nI'm on a budget, what computers do you have?\",\"ideal_answer\":{\"Televisions and Home Theater Systems\":[\"CineView 8K TV\"],\"Gaming Consoles and Accessories\":[\"GameSphere X\"],\"Computers and Laptops\":[\"TechPro Ultrabook\",\"BlueWave Gaming Laptop\",\"PowerLite Convertible\",\"TechPro Desktop\",\"BlueWave Chromebook\"]}},{\"customer_msg\":\"What smartphones do you have?\",\"ideal_answer\":{\"Smartphones and Accessories\":[\"SmartX ProPhone\",\"MobiTech PowerCase\",\"SmartX MiniPhone\",\"MobiTech Wireless Charger\",\"SmartX EarBuds\"]}},{\"customer_msg\":\"I'm on a budget. Can you recommend some smartphones to me?\",\"ideal_answer\":{\"Smartphones and Accessories\":[\"SmartX EarBuds\",\"SmartX MiniPhone\",\"MobiTech PowerCase\",\"SmartX ProPhone\",\"MobiTech Wireless Charger\"]}},{\"customer_msg\":\"What Gaming consoles would be good for my friend who is into racing games?\",\"ideal_answer\":{\"Gaming Consoles and Accessories\":[\"GameSphere X\",\"ProGamer Controller\",\"GameSphere Y\",\"ProGamer Racing Wheel\",\"GameSphere VR Headset\"]}},{\"customer_msg\":\"What could be a good present for my videographer friend?\",\"ideal_answer\":{\"Cameras and Camcorders\":[\"FotoSnap DSLR Camera\",\"ActionCam 4K\",\"FotoSnap Mirrorless Camera\",\"ZoomMaster Camcorder\",\"FotoSnap Instant Camera\"]}},{\"customer_msg\":\"I would like a hot tub time machine.\",\"ideal_answer\":[]}]";
        JSONArray array = JSONUtil.parseArray(testCase);

        JSONObject cc = (JSONObject) array.get(2);
        String customerMsg = cc.getStr("customer_msg");
        JSONObject idealAnswer = (JSONObject) cc.getJSONObject("ideal_answer");

        String result = findCategoryAndProductV2(customerMsg, allProducts);

        log.info("用户提问: {}", customerMsg);
        log.info("标准答案: {}", idealAnswer);

        evalResponseWithIdeal(result, idealAnswer);

    }


    @Test
    public void test10() {

        //测试用例
        String testCase = "[{\"customer_msg\":\"Which TV can I buy if I'm on a budget?\",\"ideal_answer\":{\"Televisions and Home Theater Systems\":[\"CineView 4K TV\",\"SoundMax Home Theater\",\"CineView 8K TV\",\"SoundMax Soundbar\",\"CineView OLED TV\"]}},{\"customer_msg\":\"I need a charger for my smartphone\",\"ideal_answer\":{\"Smartphones and Accessories\":[\"MobiTech PowerCase\",\"MobiTech Wireless Charger\",\"SmartX EarBuds\"]}},{\"customer_msg\":\"What computers do you have?\",\"ideal_answer\":{\"Computers and Laptops\":[\"TechPro Ultrabook\",\"BlueWave Gaming Laptop\",\"PowerLite Convertible\",\"TechPro Desktop\",\"BlueWave Chromebook\"]}},{\"customer_msg\":\"tell me about the smartx pro phone and the fotosnap camera, the dslr one. Also, what TVs do you have?\",\"ideal_answer\":{\"Smartphones and Accessories\":[\"SmartX ProPhone\"],\"Cameras and Camcorders\":[\"FotoSnap DSLR Camera\"],\"Televisions and Home Theater Systems\":[\"CineView 4K TV\",\"SoundMax Home Theater\",\"CineView 8K TV\",\"SoundMax Soundbar\",\"CineView OLED TV\"]}},{\"customer_msg\":\"tell me about the CineView TV, the 8K one, Gamesphere console, the X one.\\nI'm on a budget, what computers do you have?\",\"ideal_answer\":{\"Televisions and Home Theater Systems\":[\"CineView 8K TV\"],\"Gaming Consoles and Accessories\":[\"GameSphere X\"],\"Computers and Laptops\":[\"TechPro Ultrabook\",\"BlueWave Gaming Laptop\",\"PowerLite Convertible\",\"TechPro Desktop\",\"BlueWave Chromebook\"]}},{\"customer_msg\":\"What smartphones do you have?\",\"ideal_answer\":{\"Smartphones and Accessories\":[\"SmartX ProPhone\",\"MobiTech PowerCase\",\"SmartX MiniPhone\",\"MobiTech Wireless Charger\",\"SmartX EarBuds\"]}},{\"customer_msg\":\"I'm on a budget. Can you recommend some smartphones to me?\",\"ideal_answer\":{\"Smartphones and Accessories\":[\"SmartX EarBuds\",\"SmartX MiniPhone\",\"MobiTech PowerCase\",\"SmartX ProPhone\",\"MobiTech Wireless Charger\"]}},{\"customer_msg\":\"What Gaming consoles would be good for my friend who is into racing games?\",\"ideal_answer\":{\"Gaming Consoles and Accessories\":[\"GameSphere X\",\"ProGamer Controller\",\"GameSphere Y\",\"ProGamer Racing Wheel\",\"GameSphere VR Headset\"]}},{\"customer_msg\":\"What could be a good present for my videographer friend?\",\"ideal_answer\":{\"Cameras and Camcorders\":[\"FotoSnap DSLR Camera\",\"ActionCam 4K\",\"FotoSnap Mirrorless Camera\",\"ZoomMaster Camcorder\",\"FotoSnap Instant Camera\"]}},{\"customer_msg\":\"I would like a hot tub time machine.\",\"ideal_answer\":[]}]";
        JSONArray array = JSONUtil.parseArray(testCase);

        double sumScore = 0d;
        for (Object o : array) {
            JSONObject cc = (JSONObject) o;
            String customerMsg = cc.getStr("customer_msg");
            JSONObject idealAnswer = (JSONObject) cc.getJSONObject("ideal_answer");
            String result = findCategoryAndProductV2(customerMsg, allProducts);

            double score = evalResponseWithIdeal(result, idealAnswer);

            log.info("score: {}\n", score);
            sumScore = sumScore + score;
        }

        log.info("正确比例: {}/{}", array.size(), sumScore / array.size());

    }


    //从用户输入中获取到产品和类别
    public String findCategoryAndProductV1(String userInput, String productsAndCategory) {


        String system = "您将提供客户服务查询。\n" +
                "    客户服务查询将用" + delimiter + "字符分隔。\n" +
                "    输出一个 Python 列表，列表中的每个对象都是 Json 对象，每个对象的格式如下：\n" +
                "        'category': <Computers and Laptops, Smartphones and Accessories, Televisions and Home Theater Systems, \n" +
                "    Gaming Consoles and Accessories, Audio Equipment, Cameras and Camcorders中的一个>,\n" +
                "    以及\n" +
                "        'products': <必须在下面允许的产品中找到的产品列表>\n" +
                "    \n" +
                "    其中类别和产品必须在客户服务查询中找到。\n" +
                "    如果提到了一个产品，它必须与下面允许的产品列表中的正确类别关联。\n" +
                "    如果没有找到产品或类别，输出一个空列表。\n" +
                "    \n" +
                "    根据产品名称和产品类别与客户服务查询的相关性，列出所有相关的产品。\n" +
                "    不要从产品的名称中假设任何特性或属性，如相对质量或价格。\n" +
                "    \n" +
                "    允许的产品以 JSON 格式提供。\n" +
                "    每个项目的键代表类别。\n" +
                "    每个项目的值是该类别中的产品列表。\n" +
                "    允许的产品：" + productsAndCategory + "";


        String fewShotUser_1 = "我想要最贵的电脑。";
        String fewShotAssistant_1 = "[{\"category\":\"Computers and Laptops\",\"products\":[\"TechPro Ultrabook\",\"BlueWave Gaming Laptop\",\"PowerLite Convertible\",\"TechPro Desktop\",\"BlueWave Chromebook\"]}]";


        List<ChatMessage> messages = new ArrayList<>();

        ChatMessage systemMessage = new ChatMessage();
        systemMessage.setRole("system");
        systemMessage.setContent(system);
        messages.add(systemMessage);

        ChatMessage userMessage = new ChatMessage();
        userMessage.setRole("user");
        userMessage.setContent(delimiter + fewShotUser_1 + delimiter);
        messages.add(userMessage);

        ChatMessage assistantMessage = new ChatMessage();
        assistantMessage.setRole("assistant");
        assistantMessage.setContent(fewShotAssistant_1);
        messages.add(assistantMessage);

        ChatMessage userInputMessage = new ChatMessage();
        userInputMessage.setRole("user");
        userInputMessage.setContent(delimiter + userInput + delimiter);
        messages.add(userInputMessage);

        return this.getCompletionFromMessage(messages, 0);

    }


    //从用户输入中获取到产品和类别
    public String findCategoryAndProductV2(String userInput, String productsAndCategory) {


        String system = "您将提供客户服务查询。\n" +
                "    客户服务查询将用" + delimiter + "字符分隔。\n" +
                "    输出一个列表，列表中的每个对象都是 JSON 对象，每个对象的格式如下：\n" +
                "        'category': <Computers and Laptops, Smartphones and Accessories, Televisions and Home Theater Systems, \n" +
                "    Gaming Consoles and Accessories, Audio Equipment, Cameras and Camcorders中的一个>,\n" +
                "    和\n" +
                "        'products': <必须在下面允许的产品中找到的产品列表>\n" +
                "    不要输出任何不是 JSON 格式的额外文本。\n" +
                "    输出请求的 JSON 后，不要写任何解释性的文本。\n" +
                "    \n" +
                "    其中类别和产品必须在客户服务查询中找到。\n" +
                "    如果提到了一个产品，它必须与下面允许的产品列表中的正确类别关联。\n" +
                "    如果没有找到产品或类别，输出一个空列表。\n" +
                "    \n" +
                "    根据产品名称和产品类别与客户服务查询的相关性，列出所有相关的产品。\n" +
                "    不要从产品的名称中假设任何特性或属性，如相对质量或价格。\n" +
                "    \n" +
                "    允许的产品以 JSON 格式提供。\n" +
                "    每个项目的键代表类别。\n" +
                "    每个项目的值是该类别中的产品列表。\n" +
                "    允许的产品：" + productsAndCategory + "";


        String fewShotUser_1 = "我想要最贵的电脑。";
        String fewShotAssistant_1 = "[{\"category\":\"Computers and Laptops\",\"products\":[\"TechPro Ultrabook\",\"BlueWave Gaming Laptop\",\"PowerLite Convertible\",\"TechPro Desktop\",\"BlueWave Chromebook\"]}]";

        //增加了一个 few-shot 提示
        String fewShotUser_2 = "我想要最便宜的电脑。你推荐哪款？";
        String fewShotAssistant_2 = "[{\"category\":\"Computers and Laptops\",\"products\":[\"TechPro Ultrabook\",\"BlueWave Gaming Laptop\",\"PowerLite Convertible\",\"TechPro Desktop\",\"BlueWave Chromebook\"]}]";


        List<ChatMessage> messages = new ArrayList<>();

        ChatMessage systemMessage = new ChatMessage();
        systemMessage.setRole("system");
        systemMessage.setContent(system);
        messages.add(systemMessage);

        ChatMessage userMessage = new ChatMessage();
        userMessage.setRole("user");
        userMessage.setContent(delimiter + fewShotUser_1 + delimiter);
        messages.add(userMessage);

        ChatMessage assistantMessage = new ChatMessage();
        assistantMessage.setRole("assistant");
        assistantMessage.setContent(fewShotAssistant_1);
        messages.add(assistantMessage);


        ChatMessage shot2 = new ChatMessage();
        shot2.setRole("user");
        shot2.setContent(delimiter + fewShotUser_2 + delimiter);
        messages.add(shot2);

        ChatMessage shotAssistant2 = new ChatMessage();
        shotAssistant2.setRole("assistant");
        shotAssistant2.setContent(fewShotAssistant_2);
        messages.add(shotAssistant2);


        ChatMessage userInputMessage = new ChatMessage();
        userInputMessage.setRole("user");
        userInputMessage.setContent(delimiter + userInput + delimiter);
        messages.add(userInputMessage);

        return this.getCompletionFromMessage(messages, 0);

    }


    public double evalResponseWithIdeal(String response, JSONObject ideal) {

        log.info("回复：{}", response);

        if (StrUtil.isBlank(response) && ideal == null) {
            return 1;
        }

        if (StrUtil.isBlank(response) || ideal == null) {
            return 0;
        }

        //统计正确总数
        int correct = 0;

        JSONArray array = JSONUtil.parseArray(response);

        for (Object o : array) {

            JSONObject jsonObject = (JSONObject) o;

            String category = jsonObject.getStr("category");
            List<String> products = jsonObject.getBeanList("products", String.class);

            if (category != null && CollectionUtil.isNotEmpty(products)) {

                List<String> fps = ideal.getJSONArray(category).toList(String.class);

                log.info("产品集合   ：{}", products);
                log.info("标签答案集合：{}", fps);

                if (CollectionUtil.isNotEmpty(fps)) {
                    //找到的产品数组和标准答案给到的商品数组一致
                    if (fps.containsAll(products)) {
                        log.info("正确");
                        correct++;
                    } else {
                        log.info("产品集合: {}", products);
                        log.info("标准的产品集合: {}", fps);
                        if (products.size() < fps.size()) {
                            log.info("回答是标准答案的一个子集");
                        } else {
                            log.info("回答是标准答案的一个超集");
                        }
                    }
                }
            }


        }

        double pccorrect = correct / array.size();

        return pccorrect;
    }

}
