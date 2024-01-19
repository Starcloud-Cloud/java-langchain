package com.starcloud.ops.llm.langchain.core.model.multimodal.qwen;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.dashscope.aigc.generation.models.QwenParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationUsage;
import com.starcloud.ops.llm.langchain.config.QwenAIConfig;
import com.starcloud.ops.llm.langchain.core.callbacks.CallbackManagerForLLMRun;
import com.starcloud.ops.llm.langchain.core.callbacks.StreamingSseCallBackHandler;
import com.starcloud.ops.llm.langchain.core.model.chat.base.BaseChatModel;
import com.starcloud.ops.llm.langchain.core.model.chat.base.BaseMultiModalChatModel;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMUsage;
import com.starcloud.ops.llm.langchain.core.model.llm.base.ChatGeneration;
import com.starcloud.ops.llm.langchain.core.model.llm.base.ChatResult;
import com.starcloud.ops.llm.langchain.core.schema.ModelTypeEnum;
import com.starcloud.ops.llm.langchain.core.schema.message.*;
import com.starcloud.ops.llm.langchain.core.schema.message.multimodal.MultiModalMessage;
import com.starcloud.ops.llm.langchain.core.schema.tool.FunctionDescription;
import com.starcloud.ops.llm.langchain.core.utils.MessageConvert;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 多模态支持
 */
@Slf4j
@Data
public class ChatVLQwen extends BaseMultiModalChatModel<MultiModalConversationResult> {

    private static QwenAIConfig qwenAIConfig = SpringUtil.getBean("qwenAIConfig");

    private String model = ModelTypeEnum.QWEN.getName();

    private Double topP = 0.5d;

    private Integer topK = 0;

    private Boolean stream = false;

    private Boolean enableSearch = false;

    private int seed;

    private String resultFormat = "message";


    @Override
    public String getModelType() {
        return this.getModel();
    }

    @Override
    public ChatResult<MultiModalConversationResult> _generate(List<? extends BaseMessage> messages, List<String> tops, CallbackManagerForLLMRun callbackManagerForLLMRun) {

        //上游最大值是2，这里最大值是1
        Double top = Double.valueOf(NumberUtil.decimalFormat("0.0", this.getTopP() / 2));

        List<com.alibaba.dashscope.common.MultiModalMessage> chatMessages = Optional.ofNullable((List<MultiModalMessage>) messages).orElse(new ArrayList<>()).stream().map(MessageConvert::BaseMessage2QwenMessage).collect(Collectors.toList());

        MultiModalConversationParam param = MultiModalConversationParam.builder()
                .model(MultiModalConversation.Models.QWEN_VL_PLUS)
                .apiKey(qwenAIConfig.randomApiKey())
                .topP(top)
                .messages(chatMessages)
                .build();


        ChatResult chatResult = new ChatResult();
        Long requestToken = this.getNumTokensFromMessages(messages);
        BaseLLMUsage baseLLMUsage = BaseLLMUsage.builder().promptTokens(requestToken).build();
        chatResult.setUsage(baseLLMUsage);

        // log.info("ChatQwen qwenParam: {}", JsonUtils.toJson(qwenParam));

        if (this.getStream()) {

//            BaseChatModel chatModel = this;
//            StringBuffer sb = new StringBuffer();
//
//            try {
//
//                Generation gen = new Generation();
//                Semaphore semaphore = new Semaphore(0);
//                gen.streamCall(qwenParam, new ResultCallback<GenerationResult>() {
//
//                    @Override
//                    public void onEvent(GenerationResult message) {
//                        GenerationOutput t = message.getOutput();
//
//                        String msg = t.getChoices().get(0).getMessage().getContent();
//                        if (msg != null) {
//                            sb.append(msg);
//                            callbackManagerForLLMRun.onLLMNewToken(msg);
//                        }
//                        if ("stop".equals(t.getChoices().get(0).getFinishReason())) {
//                            String endString = "&end&";
//                        }
//                    }
//
//                    @Override
//                    public void onError(Exception e) {
//
//                        log.error("Qwen onError: {}", e.getMessage(), e);
//                        callbackManagerForLLMRun.onLLMError(e.getMessage(), e);
//
//                        semaphore.release();
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                        String resultMsg = sb.toString();
//
//                        Long resultToke = chatModel.getNumTokens(resultMsg);
//                        Long totalTokens = resultToke + requestToken;
//
//                        //todo usage
//                        baseLLMUsage.setCompletionTokens(resultToke).setTotalTokens(totalTokens);
//
//                        chatResult.setChatGenerations(Arrays.asList(ChatGeneration.<ChatCompletionResult>builder().chatMessage(new AIMessage(resultMsg)).usage(baseLLMUsage).build()));
//                        chatResult.setUsage(baseLLMUsage);
//
//                        semaphore.release();
//                    }
//
//                });
//
//                semaphore.acquire();
//
//            } catch (Exception e) {
//
//                throw new RuntimeException(e.getMessage(), e);
//            }

            return chatResult;

        } else {

            try {

                MultiModalConversation conv = new MultiModalConversation();

                MultiModalConversationResult result = conv.call(param);

                com.alibaba.dashscope.common.MultiModalMessage multiModalMessage = result.getOutput().getChoices().get(0).getMessage();

                MultiModalConversationUsage usage = result.getUsage();

                baseLLMUsage.setCompletionTokens(Long.valueOf(usage.getOutputTokens())).setPromptTokens(Long.valueOf(usage.getInputTokens()));
                baseLLMUsage.setTotalTokens(baseLLMUsage.getCompletionTokens() + baseLLMUsage.getPromptTokens());

                MultiModalMessage baseMessage = this.convertToMessage(multiModalMessage);
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


    private MultiModalMessage convertToMessage(com.alibaba.dashscope.common.MultiModalMessage multiModalMessage) {

        switch (multiModalMessage.getRole()) {

            case "user":
                return new com.starcloud.ops.llm.langchain.core.schema.message.multimodal.HumanMessage(multiModalMessage.getContent());
            case "assistant":

                return new com.starcloud.ops.llm.langchain.core.schema.message.multimodal.AIMessage(multiModalMessage.getContent());
            case "system":
                return new com.starcloud.ops.llm.langchain.core.schema.message.multimodal.SystemMessage(multiModalMessage.getContent());
            default:
                return new com.starcloud.ops.llm.langchain.core.schema.message.multimodal.ChatMessage(multiModalMessage.getContent(), multiModalMessage.getRole());

        }
    }


}
