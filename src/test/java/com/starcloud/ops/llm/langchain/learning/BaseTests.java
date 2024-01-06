package com.starcloud.ops.llm.langchain.learning;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starcloud.ops.llm.langchain.LangChainConfiguration;
import com.starcloud.ops.llm.langchain.config.OpenAIConfig;
import com.theokanning.openai.OpenAiApi;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.moderation.Moderation;
import com.theokanning.openai.moderation.ModerationRequest;
import com.theokanning.openai.moderation.ModerationResult;
import com.theokanning.openai.service.OpenAiService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import retrofit2.Retrofit;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.*;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static com.theokanning.openai.service.OpenAiService.*;

@Slf4j
@SpringBootTest(classes = {LangChainConfiguration.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class BaseTests {

    @MockBean
    private DataSource dataSource;

    @Value("${starcloud-langchain.model.llm.openai.apiKey}")
    private String apiKey;

    @Value("#{'${starcloud-langchain.model.llm.openai.proxyHost}'.split(',')}")
    private List<String> proxyHosts;

    @Value("${starcloud-langchain.model.llm.openai.proxyPort}")
    private int proxyPort;


    protected OpenAiService addProxy(OpenAIConfig openAIConfig) {

        if (CollectionUtil.isEmpty(openAIConfig.getProxyHosts())) {
            OpenAiService openAiService = new OpenAiService(openAIConfig.getApiKey(), Duration.ofSeconds(180));

            return openAiService;
        }

        ObjectMapper mapper = defaultObjectMapper();

        OkHttpClient client = defaultClient(openAIConfig.getApiKey(), Duration.ofSeconds(openAIConfig.getTimeOut()))
                .newBuilder()
                //.proxy(proxy)
                .proxySelector(new ProxySelector() {

                    @Override
                    public List<Proxy> select(URI uri) {

                        List<Proxy> result = Optional.ofNullable(openAIConfig.getProxyHosts()).orElse(new ArrayList<>()).stream().map(host -> {
                            return new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(host, openAIConfig.getProxyPort()));
                        }).collect(Collectors.toList());

                        return result;
                    }

                    @Override
                    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
                        log.error("gpt proxy is fail: {} ", ioe.getMessage(), ioe);
                    }
                })
                .build();
        Retrofit retrofit = defaultRetrofit(client, mapper);

        //Retrofit retrofit = defaultAzureRetrofit(client, mapper);


        OpenAiApi api = retrofit.create(OpenAiApi.class);

        return new OpenAiService(api, client.dispatcher().executorService());

    }

    //封装一个 GPT接口的函数，作为使用prompt作为参数，返回结果为GPT返回的内容
    public String getCompletion(String prompt) {

        String apiKey = this.apiKey;

        OpenAIConfig openAIConfig = new OpenAIConfig();

        openAIConfig.setApiKey(apiKey);
        openAIConfig.setProxyHosts(proxyHosts);
        openAIConfig.setProxyPort(proxyPort);
        openAIConfig.setTimeOut(180l);

        OpenAiService openAiService = addProxy(openAIConfig);

        ChatCompletionRequest chatCompletionRequest = new ChatCompletionRequest();

        chatCompletionRequest.setModel("gpt-3.5-turbo");
        chatCompletionRequest.setTemperature(0d);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRole("user");
        chatMessage.setContent(prompt);

        chatCompletionRequest.setMessages(Arrays.asList(chatMessage));

        ChatCompletionResult result = openAiService.createChatCompletion(chatCompletionRequest);

        ChatMessage chatMessage1 = result.getChoices().get(0).getMessage();

        return chatMessage1.getContent();
    }


    //传入消息列表进行请求
    public String getCompletionFromMessage(List<ChatMessage> chatMessages, double temperature) {

        return this.getCompletionFromMessage(chatMessages, temperature, 500);

    }

    //传入消息列表进行请求
    public String getCompletionFromMessage(List<ChatMessage> chatMessages, double temperature, Integer maxTokens) {

        String apiKey = this.apiKey;

        OpenAIConfig openAIConfig = new OpenAIConfig();

        openAIConfig.setApiKey(apiKey);
        openAIConfig.setProxyHosts(proxyHosts);
        openAIConfig.setProxyPort(proxyPort);
        openAIConfig.setTimeOut(180l);

        OpenAiService openAiService = addProxy(openAIConfig);

        ChatCompletionRequest chatCompletionRequest = new ChatCompletionRequest();

        chatCompletionRequest.setModel("gpt-3.5-turbo");
        chatCompletionRequest.setTemperature(temperature);
        chatCompletionRequest.setMaxTokens(maxTokens);
        chatCompletionRequest.setMessages(chatMessages);

        ChatCompletionResult result = openAiService.createChatCompletion(chatCompletionRequest);

        ChatMessage chatMessage1 = result.getChoices().get(0).getMessage();

        return chatMessage1.getContent();
    }


    public Moderation moderation(String text) {

        String apiKey = this.apiKey;

        OpenAIConfig openAIConfig = new OpenAIConfig();

        openAIConfig.setApiKey(apiKey);
        openAIConfig.setProxyHosts(proxyHosts);
        openAIConfig.setProxyPort(proxyPort);
        openAIConfig.setTimeOut(180l);

        OpenAiService openAiService = addProxy(openAIConfig);


        ModerationRequest moderationRequest = new ModerationRequest();

        //监督用对模型
        // moderationRequest.setModel("text-moderation-005");
        moderationRequest.setInput(text);

        ModerationResult result = openAiService.createModeration(moderationRequest);

        return result.getResults().get(0);

    }


    private String allProductsAndCategory = "{\"Computers and Laptops\":[\"TechPro Ultrabook\",\"BlueWave Gaming Laptop\",\"PowerLite Convertible\",\"TechPro Desktop\",\"BlueWave Chromebook\"],\"Smartphones and Accessories\":[\"SmartX ProPhone\",\"MobiTech PowerCase\",\"SmartX MiniPhone\",\"MobiTech Wireless Charger\",\"SmartX EarBuds\"],\"Televisions and Home Theater Systems\":[\"CineView 4K TV\",\"SoundMax Home Theater\",\"CineView 8K TV\",\"SoundMax Soundbar\",\"CineView OLED TV\"],\"Gaming Consoles and Accessories\":[\"GameSphere X\",\"ProGamer Controller\",\"GameSphere Y\",\"ProGamer Racing Wheel\",\"GameSphere VR Headset\"],\"Audio Equipment\":[\"AudioPhonic Noise-Canceling Headphones\",\"WaveSound Bluetooth Speaker\",\"AudioPhonic True Wireless Earbuds\",\"WaveSound Soundbar\",\"AudioPhonic Turntable\"],\"Cameras and Camcorders\":[\"FotoSnap DSLR Camera\",\"ActionCam 4K\",\"FotoSnap Mirrorless Camera\",\"ZoomMaster Camcorder\",\"FotoSnap Instant Camera\"]}";

    /**
     * 根据给到的商品和分类列表返回问题中的分类和对应商品列表
     *
     * @param userInput
     * @return
     */
    public String getProductsFromQuery(String userInput) {

        String delimiter = "###";
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
                "    允许的产品：" + allProductsAndCategory + "";

        List<ChatMessage> messages = new ArrayList<>();

        ChatMessage systemMessage = new ChatMessage();
        systemMessage.setRole("system");
        systemMessage.setContent(system);
        messages.add(systemMessage);

        ChatMessage userInputMessage = new ChatMessage();
        userInputMessage.setRole("user");
        userInputMessage.setContent(delimiter + userInput + delimiter);
        messages.add(userInputMessage);

        return getCompletionFromMessage(messages, 0);

    }


    //两个队列
    public static void main(String[] args) {

        SelfStack selfStack = new SelfStack();

        System.out.println(selfStack.pop());
        selfStack.push(1);
        selfStack.push(2);
        selfStack.push(3);
        selfStack.push(6);
        selfStack.push(9);

        System.out.println(selfStack.pop());
        System.out.println(selfStack.pop());

        System.out.println("done");
    }


    public static class SelfStack {

        private LinkedList<Integer> queue1;

        private LinkedList<Integer> queue2;

        public SelfStack() {

            this.queue1 = new LinkedList<>();
            this.queue2 = new LinkedList<>();
        }

        public void push(int x) {

            queue2.offer(x);
            while (!queue1.isEmpty()) {
                queue2.offer(queue1.poll());
            }

            LinkedList<Integer> temp = queue1;
            queue1 = queue2;
            queue2 = temp;
        }

        public Integer pop() {
            return this.empty() ? null : queue1.poll();
        }

        public Integer top() {
            return queue1.peek();
        }

        public boolean empty() {

            return queue1.isEmpty();
        }
    }


}
