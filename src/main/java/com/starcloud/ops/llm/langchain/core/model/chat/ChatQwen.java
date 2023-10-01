package com.starcloud.ops.llm.langchain.core.model.chat;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationOutput;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.GenerationUsage;
import com.alibaba.dashscope.aigc.generation.models.QwenParam;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.MessageManager;
import com.alibaba.dashscope.common.ResultCallback;
import com.starcloud.ops.llm.langchain.config.QwenAIConfig;
import com.starcloud.ops.llm.langchain.core.callbacks.CallbackManagerForLLMRun;
import com.starcloud.ops.llm.langchain.core.callbacks.StreamingSseCallBackHandler;
import com.starcloud.ops.llm.langchain.core.model.chat.base.BaseChatModel;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMUsage;
import com.starcloud.ops.llm.langchain.core.model.llm.base.ChatGeneration;
import com.starcloud.ops.llm.langchain.core.model.llm.base.ChatResult;
import com.starcloud.ops.llm.langchain.core.schema.ModelTypeEnum;
import com.starcloud.ops.llm.langchain.core.schema.message.*;
import com.starcloud.ops.llm.langchain.core.schema.tool.FunctionDescription;
import com.starcloud.ops.llm.langchain.core.utils.MessageConvert;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

/**
 * @author df007df
 */
@Slf4j
@Data
public class ChatQwen extends BaseChatModel<GenerationResult> {

    private static QwenAIConfig qwenAIConfig = SpringUtil.getBean("qwenAIConfig");

    private String model = ModelTypeEnum.QWEN.getName();

    private Double topP = 0.5d;

    private Integer topK = 0;

    private Boolean stream = false;

    private Boolean enableSearch = false;

    private int seed;

    private String resultFormat = "message";

    private MessageManager msgManager = new MessageManager(100);

    @Override
    public String getModelType() {
        return this.getModel();
    }

    @Override
    public ChatResult<GenerationResult> _generate(List<BaseMessage> messages, List<String> tops, List<FunctionDescription> functions, CallbackManagerForLLMRun callbackManagerForLLMRun) {

        QwenParam qwenParam = QwenParam.builder().model(this.getModel()).seed(this.getSeed()).model(this.getModel()).enableSearch(this.getEnableSearch()).resultFormat(this.getResultFormat()).build();
        //上游最大值是2，这里最大值是1
        Double top = Double.valueOf(NumberUtil.decimalFormat("0.0", this.getTopP() / 2));
        qwenParam.setTopP(top);
        qwenParam.setApiKey(qwenAIConfig.getApiKey());

        List<Message> chatMessages = Optional.ofNullable(messages).orElse(new ArrayList<>()).stream().map(MessageConvert::BaseMessage2QwenMessage).collect(Collectors.toList());

        Optional.ofNullable(chatMessages).orElse(new ArrayList<>()).forEach(mes -> {
            msgManager.add(mes);
        });
        qwenParam.setMessages(msgManager.get());

        ChatResult chatResult = new ChatResult();
        Long requestToken = this.getNumTokensFromMessages(messages);
        BaseLLMUsage baseLLMUsage = BaseLLMUsage.builder().promptTokens(requestToken).build();
        chatResult.setUsage(baseLLMUsage);

        // log.info("ChatQwen qwenParam: {}", JsonUtils.toJson(qwenParam));

        if (this.getStream()) {

            BaseChatModel chatModel = this;
            StringBuffer sb = new StringBuffer();

            try {

                Generation gen = new Generation();
                Semaphore semaphore = new Semaphore(0);
                gen.streamCall(qwenParam, new ResultCallback<GenerationResult>() {

                    @Override
                    public void onEvent(GenerationResult message) {
                        GenerationOutput t = message.getOutput();

                        String msg = t.getChoices().get(0).getMessage().getContent();
                        if (msg != null) {
                            sb.append(msg);
                            callbackManagerForLLMRun.onLLMNewToken(msg);
                        }
                        if ("stop".equals(t.getChoices().get(0).getFinishReason())) {
                            String endString = "&end&";
                        }
                    }

                    @Override
                    public void onError(Exception e) {

                        log.error("Qwen onError: {}", e.getMessage(), e);
                        callbackManagerForLLMRun.onLLMError(e.getMessage(), e);

                        semaphore.release();
                    }

                    @Override
                    public void onComplete() {

                        String resultMsg = sb.toString();

                        Long resultToke = chatModel.getNumTokens(resultMsg);
                        Long totalTokens = resultToke + requestToken;

                        //todo usage
                        baseLLMUsage.setCompletionTokens(resultToke).setTotalTokens(totalTokens);

                        chatResult.setChatGenerations(Arrays.asList(ChatGeneration.<ChatCompletionResult>builder().chatMessage(new AIMessage(resultMsg)).usage(baseLLMUsage).build()));
                        chatResult.setUsage(baseLLMUsage);

                        semaphore.release();
                    }

                });

                semaphore.acquire();

            } catch (Exception e) {

                throw new RuntimeException(e.getMessage(), e);
            }

            return chatResult;

        } else {

            try {

                Generation gen = new Generation();
                GenerationResult result = gen.call(qwenParam);

                Message chatMessage = result.getOutput().getChoices().get(0).getMessage();
                GenerationUsage usage = result.getUsage();

                baseLLMUsage.setCompletionTokens(Long.valueOf(usage.getOutputTokens())).setPromptTokens(Long.valueOf(usage.getInputTokens()));
                baseLLMUsage.setTotalTokens(baseLLMUsage.getCompletionTokens() + baseLLMUsage.getPromptTokens());

                BaseMessage baseMessage = this.convertToMessage(chatMessage);
                ChatGeneration chatGeneration = ChatGeneration.builder().generationInfo(result).usage(baseLLMUsage).chatMessage(baseMessage).text(baseMessage.getContent()).build();

                //因为千问走流方式获取内容会重复，不知道什么原因，所以先关闭走同步，然后一次性发送到前端
                if (callbackManagerForLLMRun.hasHandler(StreamingSseCallBackHandler.class)) {
                    callbackManagerForLLMRun.onLLMNewToken(baseMessage.getContent());
                }

                return ChatResult.data(Arrays.asList(chatGeneration), result, baseLLMUsage);

            } catch (Exception e) {

                throw new RuntimeException(e.getMessage(), e);
            }

        }
    }


    private BaseMessage convertToMessage(Message chatMessage) {

        switch (chatMessage.getRole()) {

            case "user":
                return new HumanMessage(chatMessage.getContent());
            case "assistant":
                AIMessage aiMessage = new AIMessage(chatMessage.getContent());
                return aiMessage;
            case "system":
                return new SystemMessage(chatMessage.getContent());
            default:
                return new ChatMessage(chatMessage.getContent(), chatMessage.getRole());

        }
    }


}
