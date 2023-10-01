package com.starcloud.ops.llm.langchain.learning.chagptapi.code;

import com.starcloud.ops.llm.langchain.learning.BaseTests;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.moderation.Moderation;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class Evaluation extends BaseTests {

    //分隔符
    String delimiter = "```";

    String allProducts = "{\"TechPro Ultrabook\":{\"name\":\"TechPro 超极本\",\"category\":\"电脑和笔记本\",\"brand\":\"TechPro\",\"model_number\":\"TP-UB100\",\"warranty\":\"1 year\",\"rating\":4.5,\"features\":[\"13.3-inch display\",\"8GB RAM\",\"256GB SSD\",\"Intel Core i5 处理器\"],\"description\":\"一款时尚轻便的超极本，适合日常使用。\",\"price\":799.99},\"BlueWave Gaming Laptop\":{\"name\":\"BlueWave 游戏本\",\"category\":\"电脑和笔记本\",\"brand\":\"BlueWave\",\"model_number\":\"BW-GL200\",\"warranty\":\"2 years\",\"rating\":4.7,\"features\":[\"15.6-inch display\",\"16GB RAM\",\"512GB SSD\",\"NVIDIA GeForce RTX 3060\"],\"description\":\"一款高性能的游戏笔记本电脑，提供沉浸式体验。\",\"price\":1199.99},\"PowerLite Convertible\":{\"name\":\"PowerLite Convertible\",\"category\":\"电脑和笔记本\",\"brand\":\"PowerLite\",\"model_number\":\"PL-CV300\",\"warranty\":\"1 year\",\"rating\":4.3,\"features\":[\"14-inch touchscreen\",\"8GB RAM\",\"256GB SSD\",\"360-degree hinge\"],\"description\":\"一款多功能的可转换笔记本电脑，具有灵敏的触摸屏。\",\"price\":699.99},\"TechPro Desktop\":{\"name\":\"TechPro Desktop\",\"category\":\"电脑和笔记本\",\"brand\":\"TechPro\",\"model_number\":\"TP-DT500\",\"warranty\":\"1 year\",\"rating\":4.4,\"features\":[\"Intel Core i7 processor\",\"16GB RAM\",\"1TB HDD\",\"NVIDIA GeForce GTX 1660\"],\"description\":\"一款功能强大的台式电脑，适用于工作和娱乐。\",\"price\":999.99},\"BlueWave Chromebook\":{\"name\":\"BlueWave Chromebook\",\"category\":\"电脑和笔记本\",\"brand\":\"BlueWave\",\"model_number\":\"BW-CB100\",\"warranty\":\"1 year\",\"rating\":4.1,\"features\":[\"11.6-inch display\",\"4GB RAM\",\"32GB eMMC\",\"Chrome OS\"],\"description\":\"一款紧凑而价格实惠的Chromebook，适用于日常任务。\",\"price\":249.99},\"SmartX ProPhone\":{\"name\":\"SmartX ProPhone\",\"category\":\"智能手机和配件\",\"brand\":\"SmartX\",\"model_number\":\"SX-PP10\",\"warranty\":\"1 year\",\"rating\":4.6,\"features\":[\"6.1-inch display\",\"128GB storage\",\"12MP dual camera\",\"5G\"],\"description\":\"一款拥有先进摄像功能的强大智能手机。\",\"price\":899.99},\"MobiTech PowerCase\":{\"name\":\"MobiTech PowerCase\",\"category\":\"专业手机\",\"brand\":\"MobiTech\",\"model_number\":\"MT-PC20\",\"warranty\":\"1 year\",\"rating\":4.3,\"features\":[\"5000mAh battery\",\"Wireless charging\",\"Compatible with SmartX ProPhone\"],\"description\":\"一款带有内置电池的保护手机壳，可延长使用时间。\",\"price\":59.99},\"SmartX MiniPhone\":{\"name\":\"SmartX MiniPhone\",\"category\":\"专业手机\",\"brand\":\"SmartX\",\"model_number\":\"SX-MP5\",\"warranty\":\"1 year\",\"rating\":4.2,\"features\":[\"4.7-inch display\",\"64GB storage\",\"8MP camera\",\"4G\"],\"description\":\"一款紧凑而价格实惠的智能手机，适用于基本任务。\",\"price\":399.99},\"MobiTech Wireless Charger\":{\"name\":\"MobiTech Wireless Charger\",\"category\":\"专业手机\",\"brand\":\"MobiTech\",\"model_number\":\"MT-WC10\",\"warranty\":\"1 year\",\"rating\":4.5,\"features\":[\"10W fast charging\",\"Qi-compatible\",\"LED indicator\",\"Compact design\"],\"description\":\"一款方便的无线充电器，使工作区域整洁无杂物。\",\"price\":29.99},\"SmartX EarBuds\":{\"name\":\"SmartX EarBuds\",\"category\":\"专业手机\",\"brand\":\"SmartX\",\"model_number\":\"SX-EB20\",\"warranty\":\"1 year\",\"rating\":4.4,\"features\":[\"True wireless\",\"Bluetooth 5.0\",\"Touch controls\",\"24-hour battery life\"],\"description\":\"通过这些舒适的耳塞体验真正的无线自由。\",\"price\":99.99},\"CineView 4K TV\":{\"name\":\"CineView 4K TV\",\"category\":\"电视和家庭影院系统\",\"brand\":\"CineView\",\"model_number\":\"CV-4K55\",\"warranty\":\"2 years\",\"rating\":4.8,\"features\":[\"55-inch display\",\"4K resolution\",\"HDR\",\"Smart TV\"],\"description\":\"一款色彩鲜艳、智能功能丰富的惊艳4K电视。\",\"price\":599.99},\"SoundMax Home Theater\":{\"name\":\"SoundMax Home Theater\",\"category\":\"电视和家庭影院系统\",\"brand\":\"SoundMax\",\"model_number\":\"SM-HT100\",\"warranty\":\"1 year\",\"rating\":4.4,\"features\":[\"5.1 channel\",\"1000W output\",\"Wireless subwoofer\",\"Bluetooth\"],\"description\":\"一款强大的家庭影院系统，提供沉浸式音频体验。\",\"price\":399.99},\"CineView 8K TV\":{\"name\":\"CineView 8K TV\",\"category\":\"电视和家庭影院系统\",\"brand\":\"CineView\",\"model_number\":\"CV-8K65\",\"warranty\":\"2 years\",\"rating\":4.9,\"features\":[\"65-inch display\",\"8K resolution\",\"HDR\",\"Smart TV\"],\"description\":\"通过这款惊艳的8K电视，体验未来。\",\"price\":2999.99},\"SoundMax Soundbar\":{\"name\":\"SoundMax Soundbar\",\"category\":\"电视和家庭影院系统\",\"brand\":\"SoundMax\",\"model_number\":\"SM-SB50\",\"warranty\":\"1 year\",\"rating\":4.3,\"features\":[\"2.1 channel\",\"300W output\",\"Wireless subwoofer\",\"Bluetooth\"],\"description\":\"使用这款时尚而功能强大的声音，升级您电视的音频体验。\",\"price\":199.99},\"CineView OLED TV\":{\"name\":\"CineView OLED TV\",\"category\":\"电视和家庭影院系统\",\"brand\":\"CineView\",\"model_number\":\"CV-OLED55\",\"warranty\":\"2 years\",\"rating\":4.7,\"features\":[\"55-inch display\",\"4K resolution\",\"HDR\",\"Smart TV\"],\"description\":\"通过这款OLED电视，体验真正的五彩斑斓。\",\"price\":1499.99},\"GameSphere X\":{\"name\":\"GameSphere X\",\"category\":\"游戏机和配件\",\"brand\":\"GameSphere\",\"model_number\":\"GS-X\",\"warranty\":\"1 year\",\"rating\":4.9,\"features\":[\"4K gaming\",\"1TB storage\",\"Backward compatibility\",\"Online multiplayer\"],\"description\":\"一款下一代游戏机，提供终极游戏体验。\",\"price\":499.99},\"ProGamer Controller\":{\"name\":\"ProGamer Controller\",\"category\":\"游戏机和配件\",\"brand\":\"ProGamer\",\"model_number\":\"PG-C100\",\"warranty\":\"1 year\",\"rating\":4.2,\"features\":[\"Ergonomic design\",\"Customizable buttons\",\"Wireless\",\"Rechargeable battery\"],\"description\":\"一款高品质的游戏手柄，提供精准和舒适的操作。\",\"price\":59.99},\"GameSphere Y\":{\"name\":\"GameSphere Y\",\"category\":\"游戏机和配件\",\"brand\":\"GameSphere\",\"model_number\":\"GS-Y\",\"warranty\":\"1 year\",\"rating\":4.8,\"features\":[\"4K gaming\",\"500GB storage\",\"Backward compatibility\",\"Online multiplayer\"],\"description\":\"一款体积紧凑、性能强劲的游戏机。\",\"price\":399.99},\"ProGamer Racing Wheel\":{\"name\":\"ProGamer Racing Wheel\",\"category\":\"游戏机和配件\",\"brand\":\"ProGamer\",\"model_number\":\"PG-RW200\",\"warranty\":\"1 year\",\"rating\":4.5,\"features\":[\"Force feedback\",\"Adjustable pedals\",\"Paddle shifters\",\"Compatible with GameSphere X\"],\"description\":\"使用这款逼真的赛车方向盘，提升您的赛车游戏体验。\",\"price\":249.99},\"GameSphere VR Headset\":{\"name\":\"GameSphere VR Headset\",\"category\":\"游戏机和配件\",\"brand\":\"GameSphere\",\"model_number\":\"GS-VR\",\"warranty\":\"1 year\",\"rating\":4.6,\"features\":[\"Immersive VR experience\",\"Built-in headphones\",\"Adjustable headband\",\"Compatible with GameSphere X\"],\"description\":\"通过这款舒适的VR头戴设备，进入虚拟现实的世界。\",\"price\":299.99},\"AudioPhonic Noise-Canceling Headphones\":{\"name\":\"AudioPhonic Noise-Canceling Headphones\",\"category\":\"音频设备\",\"brand\":\"AudioPhonic\",\"model_number\":\"AP-NC100\",\"warranty\":\"1 year\",\"rating\":4.6,\"features\":[\"Active noise-canceling\",\"Bluetooth\",\"20-hour battery life\",\"Comfortable fit\"],\"description\":\"通过这款降噪耳机，体验沉浸式的音效。\",\"price\":199.99},\"WaveSound Bluetooth Speaker\":{\"name\":\"WaveSound Bluetooth Speaker\",\"category\":\"音频设备\",\"brand\":\"WaveSound\",\"model_number\":\"WS-BS50\",\"warranty\":\"1 year\",\"rating\":4.5,\"features\":[\"Portable\",\"10-hour battery life\",\"Water-resistant\",\"Built-in microphone\"],\"description\":\"一款紧凑而多用途的蓝牙音箱，适用于随时随地收听音乐。\",\"price\":49.99},\"AudioPhonic True Wireless Earbuds\":{\"name\":\"AudioPhonic True Wireless Earbuds\",\"category\":\"音频设备\",\"brand\":\"AudioPhonic\",\"model_number\":\"AP-TW20\",\"warranty\":\"1 year\",\"rating\":4.4,\"features\":[\"True wireless\",\"Bluetooth 5.0\",\"Touch controls\",\"18-hour battery life\"],\"description\":\"通过这款舒适的真无线耳塞，无需线缆即可享受音乐。\",\"price\":79.99},\"WaveSound Soundbar\":{\"name\":\"WaveSound Soundbar\",\"category\":\"音频设备\",\"brand\":\"WaveSound\",\"model_number\":\"WS-SB40\",\"warranty\":\"1 year\",\"rating\":4.3,\"features\":[\"2.0 channel\",\"80W output\",\"Bluetooth\",\"Wall-mountable\"],\"description\":\"使用这款纤薄而功能强大的声音吧，升级您电视的音频体验。\",\"price\":99.99},\"AudioPhonic Turntable\":{\"name\":\"AudioPhonic Turntable\",\"category\":\"音频设备\",\"brand\":\"AudioPhonic\",\"model_number\":\"AP-TT10\",\"warranty\":\"1 year\",\"rating\":4.2,\"features\":[\"3-speed\",\"Built-in speakers\",\"Bluetooth\",\"USB recording\"],\"description\":\"通过这款现代化的唱片机，重拾您的黑胶唱片收藏。\",\"price\":149.99},\"FotoSnap DSLR Camera\":{\"name\":\"FotoSnap DSLR Camera\",\"category\":\"相机和摄像机\",\"brand\":\"FotoSnap\",\"model_number\":\"FS-DSLR200\",\"warranty\":\"1 year\",\"rating\":4.7,\"features\":[\"24.2MP sensor\",\"1080p video\",\"3-inch LCD\",\"Interchangeable lenses\"],\"description\":\"使用这款多功能的单反相机，捕捉惊艳的照片和视频。\",\"price\":599.99},\"ActionCam 4K\":{\"name\":\"ActionCam 4K\",\"category\":\"相机和摄像机\",\"brand\":\"ActionCam\",\"model_number\":\"AC-4K\",\"warranty\":\"1 year\",\"rating\":4.4,\"features\":[\"4K video\",\"Waterproof\",\"Image stabilization\",\"Wi-Fi\"],\"description\":\"使用这款坚固而紧凑的4K运动相机，记录您的冒险旅程。\",\"price\":299.99},\"FotoSnap Mirrorless Camera\":{\"name\":\"FotoSnap Mirrorless Camera\",\"category\":\"相机和摄像机\",\"brand\":\"FotoSnap\",\"model_number\":\"FS-ML100\",\"warranty\":\"1 year\",\"rating\":4.6,\"features\":[\"20.1MP sensor\",\"4K video\",\"3-inch touchscreen\",\"Interchangeable lenses\"],\"description\":\"一款具有先进功能的小巧轻便的无反相机。\",\"price\":799.99},\"ZoomMaster Camcorder\":{\"name\":\"ZoomMaster Camcorder\",\"category\":\"相机和摄像机\",\"brand\":\"ZoomMaster\",\"model_number\":\"ZM-CM50\",\"warranty\":\"1 year\",\"rating\":4.3,\"features\":[\"1080p video\",\"30x optical zoom\",\"3-inch LCD\",\"Image stabilization\"],\"description\":\"使用这款易于使用的摄像机，捕捉生活的瞬间。\",\"price\":249.99},\"FotoSnap Instant Camera\":{\"name\":\"FotoSnap Instant Camera\",\"category\":\"相机和摄像机\",\"brand\":\"FotoSnap\",\"model_number\":\"FS-IC10\",\"warranty\":\"1 year\",\"rating\":4.1,\"features\":[\"Instant prints\",\"Built-in flash\",\"Selfie mirror\",\"Battery-powered\"],\"description\":\"使用这款有趣且便携的即时相机，创造瞬间回忆。\",\"price\":69.99}}";


    @Test
    public void test1() {

        String result = processUserMessageCh("请告诉我关于 smartx pro phone 和 the fotosnap camera 的信息。另外，请告诉我关于你们的tvs的情况。", new ArrayList<>());

        log.info("result: {}", result);
    }


    //问答函数
    public String processUserMessageCh(String userInput, List<ChatMessage> chatMessages) {

        // 第一步: 使用 OpenAI 的 Moderation API 检查用户输入是否合规或者是一个注入的 Prompt
        Moderation moderation = this.moderation(userInput);

        if (moderation.flagged) {
            log.error("第一步：输入被 Moderation 拒绝");
            return "抱歉，您的请求不合规";
        }

        log.info("第一步：输入通过 Moderation 检查");

        //第二步：抽取出商品和对应的目录，类似于之前课程中的方法，做了一个封装
        String categoryAndProductResponse = findCategoryAndProductOnly(userInput, allProducts);

        log.info("第二步：抽取出商品列表");

        //第三步：查找商品对应信息
        checkProducts(categoryAndProductResponse);
        log.info("第三步：查找抽取出的商品信息");

        //第四步：根据信息生成回答
        String system = "您是一家大型电子商店的客户服务助理。\n" +
                "请以友好和乐于助人的语气回答问题，并提供简洁明了的答案。\n" +
                "请确保向用户提出相关的后续问题。";

        List<ChatMessage> messages = new ArrayList<>();

        ChatMessage systemMessage = new ChatMessage();
        systemMessage.setRole("system");
        systemMessage.setContent(system);
        messages.add(systemMessage);

        ChatMessage userMessage = new ChatMessage();
        userMessage.setRole("user");
        userMessage.setContent(delimiter + userInput + delimiter);
        messages.add(userMessage);

        ChatMessage assistant = new ChatMessage();
        assistant.setRole("assistant");
        assistant.setContent("相关商品信息:\n" + categoryAndProductResponse);
        messages.add(assistant);

        //通过附加 all_messages 实现多轮对话
        chatMessages.addAll(messages);

        String result = this.getCompletionFromMessage(messages, 0);
        log.info("第四步：生成用户回答");

        //将该轮信息加入到历史信息中
        ChatMessage assistantResult = new ChatMessage();
        assistantResult.setRole("assistant");
        assistantResult.setContent(result);
        chatMessages.add(assistantResult);

        //第五步：基于 Moderation API 检查输出是否合规
        Moderation moderationResult = this.moderation(result);
        if (moderationResult.flagged) {
            log.error("第五步：输出被 Moderation 拒绝");
            return "抱歉，我们不能提供该信息";
        }

        log.info("第五步：输出经过 Moderation 检查");

        //第六步：模型检查是否很好地回答了用户问题
        String checkUserMessage = "用户信息: " + delimiter + userInput + delimiter + " \n" +
                "代理回复: " + delimiter + result + delimiter + "\n" +
                "\n" +
                "回复是否足够回答问题\n" +
                "如果足够，回答 Y\n" +
                "如果不足够，回答 N\n" +
                "仅回答上述字母即可";

        ChatMessage userMessage2 = new ChatMessage();
        userMessage2.setRole("user");
        userMessage2.setContent(checkUserMessage);

        List<ChatMessage> checkMessage = Arrays.asList(
                systemMessage,
                userMessage2
        );
        String checkResult = this.getCompletionFromMessage(checkMessage, 0);

        log.info("第六步：模型评估该回答");

        if (checkResult.contains("Y")) {
            log.info("第七步：模型赞同了该回答.");

            return result;

        } else {
            log.info("第七步：模型不赞成该回答.");

            return "很抱歉，我无法提供您所需的信息。我将为您转接到一位人工客服代表以获取进一步帮助。";
        }

    }


    //从用户问题中抽取商品和类别
    public String findCategoryAndProductOnly(String userInput, String productsAndCategory) {

        String system = "您将获得客户服务查询。\n" +
                "    客户服务查询将使用" + delimiter + "字符分隔。\n" +
                "    输出一个可解析的Python列表，列表每一个元素是一个JSON对象，每个对象具有以下格式：\n" +
                "    'category': <包括以下几个类别：Computers and Laptops，Smartphones and Accessories，Televisions and Home Theater Systems，Gaming Consoles and Accessories，Audio Equipment，Cameras and Camcorders>\n" +
                "    以及\n" +
                "    'products': <必须是下面的允许产品列表中找到的产品列表>\n" +
                "\n" +
                "    其中类别和产品必须在客户服务查询中找到。\n" +
                "    如果提到了产品，则必须将其与允许产品列表中的正确类别关联。\n" +
                "    如果未找到任何产品或类别，则输出一个空列表。\n" +
                "    除了列表外，不要输出其他任何信息！\n" +
                "\n" +
                "    允许的产品以JSON格式提供。\n" +
                "    每个项的键表示类别。\n" +
                "    每个项的值是该类别中的产品列表。\n" +
                "    允许的产品: " + productsAndCategory;


        List<ChatMessage> messages = new ArrayList<>();

        ChatMessage systemMessage = new ChatMessage();
        systemMessage.setRole("system");
        systemMessage.setContent(system);
        messages.add(systemMessage);

        ChatMessage userMessage = new ChatMessage();
        userMessage.setRole("user");
        userMessage.setContent(delimiter + userInput + delimiter);
        messages.add(userMessage);

        return this.getCompletionFromMessage(messages, 0, 600);

    }


    //检查GPT返回结果中的商品应该是存在于商品列表中的
    public Boolean checkProducts(String categoryAndProductResponse) {
        return true;
    }

}
