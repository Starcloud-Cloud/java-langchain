package com.starcloud.ops.llm.langchain.learning.chagptapi.code;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.starcloud.ops.llm.langchain.learning.BaseTests;
import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.*;

@Slf4j
public class Pcp extends BaseTests {

    private String delimiter = "###";

    private String system = "你将提供服务查询。\n" +
            "服务查询将使用" + delimiter + "字符分隔。\n" +
            "\n" +
            "仅输出一个 Python 对象列表，其中每个对象具有以下格式：\n" +
            "    'category': <计算机和笔记本电脑、智能手机和配件、电视和家庭影院系统、游戏机和配件、音频设备、相机和摄像机中的一个>,\n" +
            "或者\n" +
            "    'products': <必须在下面的允许产品列表中找到的产品列表>\n" +
            "\n" +
            "类别和产品必须在客户服务查询中找到。\n" +
            "如果提及了产品，则必须将其与允许产品列表中的正确类别相关联。\n" +
            "如果未找到产品或类别，则输出空列表。\n" +
            "\n" +
            "允许的产品：\n" +
            "\n" +
            "计算机和笔记本电脑类别：\n" +
            "TechPro Ultrabook\n" +
            "BlueWave Gaming Laptop\n" +
            "PowerLite Convertible\n" +
            "TechPro Desktop\n" +
            "BlueWave Chromebook\n" +
            "\n" +
            "智能手机和配件类别：\n" +
            "SmartX ProPhone\n" +
            "MobiTech PowerCase\n" +
            "SmartX MiniPhone\n" +
            "MobiTech Wireless Charger\n" +
            "SmartX EarBuds\n" +
            "\n" +
            "电视和家庭影院系统类别：\n" +
            "CineView 4K TV\n" +
            "SoundMax Home Theater\n" +
            "CineView 8K TV\n" +
            "SoundMax Soundbar\n" +
            "CineView OLED TV\n" +
            "c\n" +
            "游戏机和配件类别：\n" +
            "GameSphere X\n" +
            "ProGamer Controller\n" +
            "GameSphere Y\n" +
            "ProGamer Racing Wheel\n" +
            "GameSphere VR Headset\n" +
            "\n" +
            "音频设备类别：\n" +
            "AudioPhonic Noise-Canceling Headphones\n" +
            "WaveSound Bluetooth Speaker\n" +
            "AudioPhonic True Wireless Earbuds\n" +
            "WaveSound Soundbar\n" +
            "AudioPhonic Turntable\n" +
            "\n" +
            "相机和摄像机类别：\n" +
            "FotoSnap DSLR Camera\n" +
            "ActionCam 4K\n" +
            "FotoSnap Mirrorless Camera\n" +
            "ZoomMaster Camcorder\n" +
            "FotoSnap Instant Camera\n" +
            "\n" +
            "仅输出 Python 对象列表，不包含其他字符信息。";

    @Test
    public void test1() {

        String user = delimiter + "请查询 SmartX ProPhone 智能手机和 FotoSnap 相机，包括单反相机。\n" +
                " 另外，请查询关于电视产品的信息。" + delimiter;

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

        String user = delimiter + "我的路由器坏了" + delimiter;

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


    String allProducts = "{\"TechPro Ultrabook\":{\"name\":\"TechPro 超极本\",\"category\":\"电脑和笔记本\",\"brand\":\"TechPro\",\"model_number\":\"TP-UB100\",\"warranty\":\"1 year\",\"rating\":4.5,\"features\":[\"13.3-inch display\",\"8GB RAM\",\"256GB SSD\",\"Intel Core i5 处理器\"],\"description\":\"一款时尚轻便的超极本，适合日常使用。\",\"price\":799.99},\"BlueWave Gaming Laptop\":{\"name\":\"BlueWave 游戏本\",\"category\":\"电脑和笔记本\",\"brand\":\"BlueWave\",\"model_number\":\"BW-GL200\",\"warranty\":\"2 years\",\"rating\":4.7,\"features\":[\"15.6-inch display\",\"16GB RAM\",\"512GB SSD\",\"NVIDIA GeForce RTX 3060\"],\"description\":\"一款高性能的游戏笔记本电脑，提供沉浸式体验。\",\"price\":1199.99},\"PowerLite Convertible\":{\"name\":\"PowerLite Convertible\",\"category\":\"电脑和笔记本\",\"brand\":\"PowerLite\",\"model_number\":\"PL-CV300\",\"warranty\":\"1 year\",\"rating\":4.3,\"features\":[\"14-inch touchscreen\",\"8GB RAM\",\"256GB SSD\",\"360-degree hinge\"],\"description\":\"一款多功能的可转换笔记本电脑，具有灵敏的触摸屏。\",\"price\":699.99},\"TechPro Desktop\":{\"name\":\"TechPro Desktop\",\"category\":\"电脑和笔记本\",\"brand\":\"TechPro\",\"model_number\":\"TP-DT500\",\"warranty\":\"1 year\",\"rating\":4.4,\"features\":[\"Intel Core i7 processor\",\"16GB RAM\",\"1TB HDD\",\"NVIDIA GeForce GTX 1660\"],\"description\":\"一款功能强大的台式电脑，适用于工作和娱乐。\",\"price\":999.99},\"BlueWave Chromebook\":{\"name\":\"BlueWave Chromebook\",\"category\":\"电脑和笔记本\",\"brand\":\"BlueWave\",\"model_number\":\"BW-CB100\",\"warranty\":\"1 year\",\"rating\":4.1,\"features\":[\"11.6-inch display\",\"4GB RAM\",\"32GB eMMC\",\"Chrome OS\"],\"description\":\"一款紧凑而价格实惠的Chromebook，适用于日常任务。\",\"price\":249.99},\"SmartX ProPhone\":{\"name\":\"SmartX ProPhone\",\"category\":\"智能手机和配件\",\"brand\":\"SmartX\",\"model_number\":\"SX-PP10\",\"warranty\":\"1 year\",\"rating\":4.6,\"features\":[\"6.1-inch display\",\"128GB storage\",\"12MP dual camera\",\"5G\"],\"description\":\"一款拥有先进摄像功能的强大智能手机。\",\"price\":899.99},\"MobiTech PowerCase\":{\"name\":\"MobiTech PowerCase\",\"category\":\"专业手机\",\"brand\":\"MobiTech\",\"model_number\":\"MT-PC20\",\"warranty\":\"1 year\",\"rating\":4.3,\"features\":[\"5000mAh battery\",\"Wireless charging\",\"Compatible with SmartX ProPhone\"],\"description\":\"一款带有内置电池的保护手机壳，可延长使用时间。\",\"price\":59.99},\"SmartX MiniPhone\":{\"name\":\"SmartX MiniPhone\",\"category\":\"专业手机\",\"brand\":\"SmartX\",\"model_number\":\"SX-MP5\",\"warranty\":\"1 year\",\"rating\":4.2,\"features\":[\"4.7-inch display\",\"64GB storage\",\"8MP camera\",\"4G\"],\"description\":\"一款紧凑而价格实惠的智能手机，适用于基本任务。\",\"price\":399.99},\"MobiTech Wireless Charger\":{\"name\":\"MobiTech Wireless Charger\",\"category\":\"专业手机\",\"brand\":\"MobiTech\",\"model_number\":\"MT-WC10\",\"warranty\":\"1 year\",\"rating\":4.5,\"features\":[\"10W fast charging\",\"Qi-compatible\",\"LED indicator\",\"Compact design\"],\"description\":\"一款方便的无线充电器，使工作区域整洁无杂物。\",\"price\":29.99},\"SmartX EarBuds\":{\"name\":\"SmartX EarBuds\",\"category\":\"专业手机\",\"brand\":\"SmartX\",\"model_number\":\"SX-EB20\",\"warranty\":\"1 year\",\"rating\":4.4,\"features\":[\"True wireless\",\"Bluetooth 5.0\",\"Touch controls\",\"24-hour battery life\"],\"description\":\"通过这些舒适的耳塞体验真正的无线自由。\",\"price\":99.99},\"CineView 4K TV\":{\"name\":\"CineView 4K TV\",\"category\":\"电视和家庭影院系统\",\"brand\":\"CineView\",\"model_number\":\"CV-4K55\",\"warranty\":\"2 years\",\"rating\":4.8,\"features\":[\"55-inch display\",\"4K resolution\",\"HDR\",\"Smart TV\"],\"description\":\"一款色彩鲜艳、智能功能丰富的惊艳4K电视。\",\"price\":599.99},\"SoundMax Home Theater\":{\"name\":\"SoundMax Home Theater\",\"category\":\"电视和家庭影院系统\",\"brand\":\"SoundMax\",\"model_number\":\"SM-HT100\",\"warranty\":\"1 year\",\"rating\":4.4,\"features\":[\"5.1 channel\",\"1000W output\",\"Wireless subwoofer\",\"Bluetooth\"],\"description\":\"一款强大的家庭影院系统，提供沉浸式音频体验。\",\"price\":399.99},\"CineView 8K TV\":{\"name\":\"CineView 8K TV\",\"category\":\"电视和家庭影院系统\",\"brand\":\"CineView\",\"model_number\":\"CV-8K65\",\"warranty\":\"2 years\",\"rating\":4.9,\"features\":[\"65-inch display\",\"8K resolution\",\"HDR\",\"Smart TV\"],\"description\":\"通过这款惊艳的8K电视，体验未来。\",\"price\":2999.99},\"SoundMax Soundbar\":{\"name\":\"SoundMax Soundbar\",\"category\":\"电视和家庭影院系统\",\"brand\":\"SoundMax\",\"model_number\":\"SM-SB50\",\"warranty\":\"1 year\",\"rating\":4.3,\"features\":[\"2.1 channel\",\"300W output\",\"Wireless subwoofer\",\"Bluetooth\"],\"description\":\"使用这款时尚而功能强大的声音，升级您电视的音频体验。\",\"price\":199.99},\"CineView OLED TV\":{\"name\":\"CineView OLED TV\",\"category\":\"电视和家庭影院系统\",\"brand\":\"CineView\",\"model_number\":\"CV-OLED55\",\"warranty\":\"2 years\",\"rating\":4.7,\"features\":[\"55-inch display\",\"4K resolution\",\"HDR\",\"Smart TV\"],\"description\":\"通过这款OLED电视，体验真正的五彩斑斓。\",\"price\":1499.99},\"GameSphere X\":{\"name\":\"GameSphere X\",\"category\":\"游戏机和配件\",\"brand\":\"GameSphere\",\"model_number\":\"GS-X\",\"warranty\":\"1 year\",\"rating\":4.9,\"features\":[\"4K gaming\",\"1TB storage\",\"Backward compatibility\",\"Online multiplayer\"],\"description\":\"一款下一代游戏机，提供终极游戏体验。\",\"price\":499.99},\"ProGamer Controller\":{\"name\":\"ProGamer Controller\",\"category\":\"游戏机和配件\",\"brand\":\"ProGamer\",\"model_number\":\"PG-C100\",\"warranty\":\"1 year\",\"rating\":4.2,\"features\":[\"Ergonomic design\",\"Customizable buttons\",\"Wireless\",\"Rechargeable battery\"],\"description\":\"一款高品质的游戏手柄，提供精准和舒适的操作。\",\"price\":59.99},\"GameSphere Y\":{\"name\":\"GameSphere Y\",\"category\":\"游戏机和配件\",\"brand\":\"GameSphere\",\"model_number\":\"GS-Y\",\"warranty\":\"1 year\",\"rating\":4.8,\"features\":[\"4K gaming\",\"500GB storage\",\"Backward compatibility\",\"Online multiplayer\"],\"description\":\"一款体积紧凑、性能强劲的游戏机。\",\"price\":399.99},\"ProGamer Racing Wheel\":{\"name\":\"ProGamer Racing Wheel\",\"category\":\"游戏机和配件\",\"brand\":\"ProGamer\",\"model_number\":\"PG-RW200\",\"warranty\":\"1 year\",\"rating\":4.5,\"features\":[\"Force feedback\",\"Adjustable pedals\",\"Paddle shifters\",\"Compatible with GameSphere X\"],\"description\":\"使用这款逼真的赛车方向盘，提升您的赛车游戏体验。\",\"price\":249.99},\"GameSphere VR Headset\":{\"name\":\"GameSphere VR Headset\",\"category\":\"游戏机和配件\",\"brand\":\"GameSphere\",\"model_number\":\"GS-VR\",\"warranty\":\"1 year\",\"rating\":4.6,\"features\":[\"Immersive VR experience\",\"Built-in headphones\",\"Adjustable headband\",\"Compatible with GameSphere X\"],\"description\":\"通过这款舒适的VR头戴设备，进入虚拟现实的世界。\",\"price\":299.99},\"AudioPhonic Noise-Canceling Headphones\":{\"name\":\"AudioPhonic Noise-Canceling Headphones\",\"category\":\"音频设备\",\"brand\":\"AudioPhonic\",\"model_number\":\"AP-NC100\",\"warranty\":\"1 year\",\"rating\":4.6,\"features\":[\"Active noise-canceling\",\"Bluetooth\",\"20-hour battery life\",\"Comfortable fit\"],\"description\":\"通过这款降噪耳机，体验沉浸式的音效。\",\"price\":199.99},\"WaveSound Bluetooth Speaker\":{\"name\":\"WaveSound Bluetooth Speaker\",\"category\":\"音频设备\",\"brand\":\"WaveSound\",\"model_number\":\"WS-BS50\",\"warranty\":\"1 year\",\"rating\":4.5,\"features\":[\"Portable\",\"10-hour battery life\",\"Water-resistant\",\"Built-in microphone\"],\"description\":\"一款紧凑而多用途的蓝牙音箱，适用于随时随地收听音乐。\",\"price\":49.99},\"AudioPhonic True Wireless Earbuds\":{\"name\":\"AudioPhonic True Wireless Earbuds\",\"category\":\"音频设备\",\"brand\":\"AudioPhonic\",\"model_number\":\"AP-TW20\",\"warranty\":\"1 year\",\"rating\":4.4,\"features\":[\"True wireless\",\"Bluetooth 5.0\",\"Touch controls\",\"18-hour battery life\"],\"description\":\"通过这款舒适的真无线耳塞，无需线缆即可享受音乐。\",\"price\":79.99},\"WaveSound Soundbar\":{\"name\":\"WaveSound Soundbar\",\"category\":\"音频设备\",\"brand\":\"WaveSound\",\"model_number\":\"WS-SB40\",\"warranty\":\"1 year\",\"rating\":4.3,\"features\":[\"2.0 channel\",\"80W output\",\"Bluetooth\",\"Wall-mountable\"],\"description\":\"使用这款纤薄而功能强大的声音吧，升级您电视的音频体验。\",\"price\":99.99},\"AudioPhonic Turntable\":{\"name\":\"AudioPhonic Turntable\",\"category\":\"音频设备\",\"brand\":\"AudioPhonic\",\"model_number\":\"AP-TT10\",\"warranty\":\"1 year\",\"rating\":4.2,\"features\":[\"3-speed\",\"Built-in speakers\",\"Bluetooth\",\"USB recording\"],\"description\":\"通过这款现代化的唱片机，重拾您的黑胶唱片收藏。\",\"price\":149.99},\"FotoSnap DSLR Camera\":{\"name\":\"FotoSnap DSLR Camera\",\"category\":\"相机和摄像机\",\"brand\":\"FotoSnap\",\"model_number\":\"FS-DSLR200\",\"warranty\":\"1 year\",\"rating\":4.7,\"features\":[\"24.2MP sensor\",\"1080p video\",\"3-inch LCD\",\"Interchangeable lenses\"],\"description\":\"使用这款多功能的单反相机，捕捉惊艳的照片和视频。\",\"price\":599.99},\"ActionCam 4K\":{\"name\":\"ActionCam 4K\",\"category\":\"相机和摄像机\",\"brand\":\"ActionCam\",\"model_number\":\"AC-4K\",\"warranty\":\"1 year\",\"rating\":4.4,\"features\":[\"4K video\",\"Waterproof\",\"Image stabilization\",\"Wi-Fi\"],\"description\":\"使用这款坚固而紧凑的4K运动相机，记录您的冒险旅程。\",\"price\":299.99},\"FotoSnap Mirrorless Camera\":{\"name\":\"FotoSnap Mirrorless Camera\",\"category\":\"相机和摄像机\",\"brand\":\"FotoSnap\",\"model_number\":\"FS-ML100\",\"warranty\":\"1 year\",\"rating\":4.6,\"features\":[\"20.1MP sensor\",\"4K video\",\"3-inch touchscreen\",\"Interchangeable lenses\"],\"description\":\"一款具有先进功能的小巧轻便的无反相机。\",\"price\":799.99},\"ZoomMaster Camcorder\":{\"name\":\"ZoomMaster Camcorder\",\"category\":\"相机和摄像机\",\"brand\":\"ZoomMaster\",\"model_number\":\"ZM-CM50\",\"warranty\":\"1 year\",\"rating\":4.3,\"features\":[\"1080p video\",\"30x optical zoom\",\"3-inch LCD\",\"Image stabilization\"],\"description\":\"使用这款易于使用的摄像机，捕捉生活的瞬间。\",\"price\":249.99},\"FotoSnap Instant Camera\":{\"name\":\"FotoSnap Instant Camera\",\"category\":\"相机和摄像机\",\"brand\":\"FotoSnap\",\"model_number\":\"FS-IC10\",\"warranty\":\"1 year\",\"rating\":4.1,\"features\":[\"Instant prints\",\"Built-in flash\",\"Selfie mirror\",\"Battery-powered\"],\"description\":\"使用这款有趣且便携的即时相机，创造瞬间回忆。\",\"price\":69.99}}";

    //通过产品名称查询商品
    public JSONObject getProductByName(String name) {

        JSONObject jsonObject = JSONUtil.parseObj(allProducts);

        return jsonObject.getJSONObject(name);
    }

    //通过商品分类查询商品列表
    public List<JSONObject> getProductsByCategory(String category) {

        List<JSONObject> products = new ArrayList<>();

        JSONObject jsonObject = JSONUtil.parseObj(allProducts);

        for (Iterator<Map.Entry<String, Object>> it = jsonObject.iterator(); it.hasNext(); ) {
            Map.Entry entry = it.next();

            JSONObject object = (JSONObject) entry.getValue();
            String cat = object.getStr("category");
            if (category.equals(cat)) {
                products.add(object);
            }
        }

        return products;
    }

    @Test
    public void test3() {

        JSONObject jsonObject = getProductByName("TechPro Ultrabook");
        log.info("test3-product:\n{}", jsonObject);

        List<JSONObject> products = getProductsByCategory("电脑和笔记本");
        log.info("test3-products:\n{}", products);
    }


    @Test
    public void test4() {

        String system = "您是一家大型电子商店的客服助理。\n" +
                "请以友好和乐于助人的口吻回答问题，并尽量简洁明了。\n" +
                "请确保向用户提出相关的后续问题。";

        String user = "请介绍一下 TechPro 超极本。\n" +
                "另外，介绍关于台式电脑产品的信息。";

        List<ChatMessage> messages = new ArrayList<>();

        ChatMessage systemMessage = new ChatMessage();
        systemMessage.setRole("system");
        systemMessage.setContent(system);
        messages.add(systemMessage);

        ChatMessage userMessage = new ChatMessage();
        userMessage.setRole("user");
        userMessage.setContent(user);
        messages.add(userMessage);

        List<JSONObject> products = getProductsByCategory("电脑和笔记本");

        ChatMessage assistant = new ChatMessage();
        assistant.setRole("assistant");
        assistant.setContent("相关产品信息:\n" + JSONUtil.toJsonStr(products));
        messages.add(assistant);

        String message = this.getCompletionFromMessage(messages, 0);

        log.info("test4: {}", message);


    }


}
