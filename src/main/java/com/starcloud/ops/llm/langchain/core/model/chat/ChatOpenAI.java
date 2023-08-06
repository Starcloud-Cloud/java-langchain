package com.starcloud.ops.llm.langchain.core.model.chat;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starcloud.ops.llm.langchain.config.OpenAIConfig;
import com.starcloud.ops.llm.langchain.core.callbacks.CallbackManagerForLLMRun;
import com.starcloud.ops.llm.langchain.core.model.chat.base.BaseChatModel;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import com.starcloud.ops.llm.langchain.core.model.llm.azure.AuthenticationInterceptor;
import com.starcloud.ops.llm.langchain.core.model.llm.azure.AzureAiApi;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMUsage;
import com.starcloud.ops.llm.langchain.core.model.llm.base.ChatGeneration;
import com.starcloud.ops.llm.langchain.core.model.llm.base.ChatResult;
import com.starcloud.ops.llm.langchain.core.schema.message.*;
import com.starcloud.ops.llm.langchain.core.schema.tool.FunctionDescription;
import com.starcloud.ops.llm.langchain.core.utils.MessageConvert;
import com.theokanning.openai.OpenAiApi;
import com.theokanning.openai.Usage;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatFunction;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

import static com.theokanning.openai.service.OpenAiService.*;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.net.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author df007df
 */
@Slf4j
@Data
public class ChatOpenAI extends BaseChatModel<ChatCompletionResult> {

    private String model = "gpt-3.5-turbo";

    private List<ChatMessage> messages;

    private Double temperature = 0.7d;

    private Double topP = 1d;

    private Integer n = 1;

    private Boolean stream = false;

    private List<String> stop;

    private Integer maxTokens = 500;

    private Double presencePenalty = 0d;

    private Double frequencyPenalty = 0d;

    private List<ChatFunction> functions;


    @Override
    public ChatResult<ChatCompletionResult> _generate(List<BaseMessage> messages, List<String> tops, List<FunctionDescription> functions, CallbackManagerForLLMRun callbackManagerForLLMRun) {

        OpenAIConfig openAIConfig = SpringUtil.getBean(OpenAIConfig.class);

        OpenAiService openAiService;

        if (StrUtil.isNotBlank(openAIConfig.getProxyHost())) {
            openAiService = addProxy(openAIConfig);
        } else if (Boolean.TRUE.equals(openAIConfig.getAzure())) {
            openAiService = azureAiService(openAIConfig);
        } else {
            openAiService = new OpenAiService(openAIConfig.getApiKey(), Duration.ofSeconds(openAIConfig.getTimeOut()));
        }

        ChatCompletionRequest chatCompletionRequest = BeanUtil.toBean(this, ChatCompletionRequest.class);

        List<ChatMessage> chatMessages = Optional.ofNullable(messages).orElse(new ArrayList<>()).stream().map(MessageConvert::BaseMessage2ChatMessage).collect(Collectors.toList());

        chatCompletionRequest.setMessages(chatMessages);

        if (chatCompletionRequest.getStream()) {

            ChatResult chatResult = new ChatResult();

            Long requestToken = this.getNumTokensFromMessages(messages);
            BaseLLMUsage baseLLMUsage = BaseLLMUsage.builder().promptTokens(requestToken).build();
            chatResult.setUsage(baseLLMUsage);

            StringBuffer sb = new StringBuffer();

            ChatOpenAI chatOpenAI = this;

            openAiService.streamChatCompletion(chatCompletionRequest)
//                    .subscribe(new FlowableSubscriber<ChatCompletionChunk>() {
//
//                        @Override
//                        public void onSubscribe(Subscription s) {
//
//                        }
//
//                        @Override
//                        public void onNext(ChatCompletionChunk t) {
//
//                            String msg = t.getChoices().get(0).getMessage().getContent();
//                            if (msg != null) {
//                                sb.append(msg);
//                                chatOpenAI.getCallbackManager().onLLMNewToken(msg);
//                            }
//                            if ("stop".equals(t.getChoices().get(0).getFinishReason())) {
//
//                                String endString = "&end&";
//
//                                chatOpenAI.getCallbackManager().onLLMNewToken(endString);
//
////                            this.getCallbackManager().onLLMEnd("stop");
//
//                            }
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//
//                            log.error("chat stream error:", e);
//
//                            chatOpenAI.getCallbackManager().onLLMError(e.getMessage(), e);
//                        }
//
//                        @Override
//                        public void onComplete() {
//
//                            String resultMsg = sb.toString();
//
//                            Long resultToke = chatOpenAI.getNumTokens(resultMsg);
//                            Long totalTokens = resultToke + requestToken;
//
//                            //todo usage
//                            baseLLMUsage.setCompletionTokens(resultToke).setTotalTokens(totalTokens);
//
//                            chatResult.setChatGenerations(Arrays.asList(ChatGeneration.builder().chatMessage(AIMessage.builder().content(resultMsg).build()).usage(baseLLMUsage).build()));
//                            chatResult.setUsage(baseLLMUsage);
//
//                            chatOpenAI.getCallbackManager().onLLMEnd("complete", resultMsg, totalTokens);
//                        }
//                    });
                    .doOnError(e -> {

                        log.error("openAiService doOnError: {}", e.getMessage(), e);

                        callbackManagerForLLMRun.onLLMError(e.getMessage(), e);

                    })
                    .doOnComplete(() -> {

                        String resultMsg = sb.toString();

                        Long resultToke = this.getNumTokens(resultMsg);
                        Long totalTokens = resultToke + requestToken;

                        //todo usage
                        baseLLMUsage.setCompletionTokens(resultToke).setTotalTokens(totalTokens);

                        chatResult.setChatGenerations(Arrays.asList(ChatGeneration.<ChatCompletionResult>builder().chatMessage(new AIMessage(resultMsg)).usage(baseLLMUsage).build()));
                        chatResult.setUsage(baseLLMUsage);

                        //callbackManagerForLLMRun.onLLMEnd("complete", resultMsg, totalTokens);
                    })
                    .doFinally(() -> {

                        String resultMsg = sb.toString();

                        if (chatResult.getUsage() == null) {

                            Long resultToke = this.getNumTokens(resultMsg);
                            Long totalTokens = resultToke + requestToken;

                            //todo usage
                            baseLLMUsage.setCompletionTokens(resultToke).setTotalTokens(totalTokens);

                            chatResult.setChatGenerations(Arrays.asList(ChatGeneration.<ChatCompletionResult>builder().chatMessage(new AIMessage(resultMsg)).usage(baseLLMUsage).build()));
                            chatResult.setUsage(baseLLMUsage);
                        }

                        openAiService.shutdownExecutor();

                        //callbackManagerForLLMRun.onLLMEnd("finally", resultMsg);

                    })
                    .blockingForEach(t -> {
                        String msg = t.getChoices().get(0).getMessage().getContent();
                        if (msg != null) {
                            sb.append(msg);
                            callbackManagerForLLMRun.onLLMNewToken(msg);
                        }
                        if ("stop".equals(t.getChoices().get(0).getFinishReason())) {

                            String endString = "&end&";

                            //callbackManagerForLLMRun.onLLMNewToken(endString);

                            //callbackManagerForLLMRun.onLLMEnd("stop");

                        }
                    });

            return chatResult;

        } else {

            if (CollectionUtil.isNotEmpty(functions)) {

                List<ChatFunction> chatFunctions = Optional.ofNullable(functions).orElse(new ArrayList<>()).stream().map(functionDescription -> {

                    ChatFunction chatFunction = new ChatFunction(functionDescription.getName());

                    chatFunction.setDescription(functionDescription.getDescription());
                    chatFunction.setParametersSchema(functionDescription.getJsonSchema());

                    return chatFunction;
//                    return ChatFunction.builder()
//                            .name(functionDescription.getName())
//                            .description(functionDescription.getDescription())
//                            .executor(functionDescription.getParameters(), null).build();

                }).collect(Collectors.toList());

                chatCompletionRequest.setFunctions(chatFunctions);
            }

            ChatCompletionResult chatCompletionResult = openAiService.createChatCompletion(chatCompletionRequest);

            ChatMessage chatMessage = chatCompletionResult.getChoices().get(0).getMessage();
            Usage usage = chatCompletionResult.getUsage();

            BaseLLMUsage baseLLMUsage = BaseLLMUsage.builder()
                    .promptTokens(usage.getPromptTokens())
                    .completionTokens(usage.getCompletionTokens())
                    .totalTokens(usage.getTotalTokens())
                    .build();

            BaseMessage baseMessage = this.convertToMessage(chatMessage);
            ChatGeneration chatGeneration = ChatGeneration.builder().generationInfo(chatCompletionResult).usage(baseLLMUsage).chatMessage(baseMessage).text(baseMessage.getContent()).build();

            return ChatResult.data(Arrays.asList(chatGeneration), chatCompletionResult, baseLLMUsage);

        }
    }


    protected OpenAiService addProxy(OpenAIConfig openAIConfig) {

        ObjectMapper mapper = defaultObjectMapper();

        OkHttpClient client = defaultClient(openAIConfig.getApiKey(), Duration.ofSeconds(openAIConfig.getTimeOut()))
                .newBuilder()
                //.proxy(proxy)
                .proxySelector(new ProxySelector() {

                    @Override
                    public List<Proxy> select(URI uri) {
                        List<Proxy> result = new ArrayList<>();
                        result.add(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(openAIConfig.getProxyHost(), openAIConfig.getProxyPort())));
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

    private static OkHttpClient defaultAzureClient(String token, Duration timeout) {

        return new OkHttpClient.Builder()
                .addInterceptor(new AuthenticationInterceptor(token))
                .connectionPool(new ConnectionPool(5, 1, TimeUnit.SECONDS))
                .readTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
                .build();
    }

    private static OpenAiService azureAiService(OpenAIConfig openAIConfig) {

        String token = openAIConfig.getAzureKey();
        Duration timeout = Duration.ofSeconds(openAIConfig.getTimeOut());

        ObjectMapper mapper = defaultObjectMapper();

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthenticationInterceptor(token))
                .connectionPool(new ConnectionPool(5, 1, TimeUnit.SECONDS))
                .readTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://moredeal.openai.azure.com/openai/deployments/mofaai/")
                //.baseUrl("https://api.openai.com/")
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        AzureAiApi api = retrofit.create(AzureAiApi.class);

        return new OpenAiService(api, client.dispatcher().executorService());
    }


    private BaseMessage convertToMessage(ChatMessage chatMessage) {

        switch (chatMessage.getRole()) {

            case "user":
                return new HumanMessage(chatMessage.getContent());
            case "assistant":
//                if (chatMessage.getFunctionCall() != null) {
//                    return new FunctionMessage(chatMessage.getFunctionCall().getName(), chatMessage.getFunctionCall().getArguments());
//                }
                AIMessage aiMessage =  new AIMessage(chatMessage.getContent());
                if (chatMessage.getFunctionCall() != null) {
                    aiMessage.getAdditionalArgs().put("function_call", chatMessage.getFunctionCall());
                }
                return aiMessage;
            case "system":
                return new SystemMessage(chatMessage.getContent());
            case "function":
                return new FunctionMessage(chatMessage.getFunctionCall().getName(), chatMessage.getFunctionCall().getArguments());
            default:
                return new com.starcloud.ops.llm.langchain.core.schema.message.ChatMessage(chatMessage.getContent(), chatMessage.getRole());

        }
    }

}
