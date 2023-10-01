package com.starcloud.ops.llm.langchain.core.memory.summary;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.knuddels.jtokkit.api.ModelType;
import com.starcloud.ops.llm.langchain.core.chain.LLMChain;
import com.starcloud.ops.llm.langchain.core.memory.BaseChatMemory;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;
import com.starcloud.ops.llm.langchain.core.model.chat.base.BaseChatModel;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLM;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.PromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.BaseLanguageModel;
import com.starcloud.ops.llm.langchain.core.schema.ModelTypeEnum;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.SystemMessage;
import com.starcloud.ops.llm.langchain.core.schema.prompt.BasePromptTemplate;
import com.starcloud.ops.llm.langchain.core.utils.TokenUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Data
public abstract class SummarizerMixin extends BaseChatMemory {

    private String humanPrefix;

    private String aiPrefix;

    private BaseLanguageModel llm;

    private Class<? extends BaseMessage> summaryMessageCls = SystemMessage.class;

    private static final String DEFAULT_SUMMARIZER_TEMPLATE = "Progressively summarize the lines of conversation provided, adding onto the previous summary returning a new summary." +
            "\n" +
            "EXAMPLE\n" +
            "Current summary:\n" +
            "The human asks what the AI thinks of artificial intelligence. The AI thinks artificial intelligence is a force for good.\n" +
            "\n" +
            "New lines of conversation:\n" +
            "Human: Why do you think artificial intelligence is a force for good?\n" +
            "AI: Because artificial intelligence will help humans reach their full potential.\n" +
            "\n" +
            "New summary:\n" +
            "The human asks what the AI thinks of artificial intelligence. The AI thinks artificial intelligence is a force for good because it will help humans reach their full potential.\n" +
            "END OF EXAMPLE\n" +
            "\n" +
            "Current summary:\n" +
            "{summary}\n" +
            "\n" +
            "New lines of conversation:\n" +
            "[lines]\n" +
            "{new_lines}\n" +
            "[End lines]" +
            "\n" +
            "Identify the language used in the conversation and use the same language in the summary! The summary is limited to {max_tokens} characters!" +
            "\n" +
            "New summary:";


    private static final String DEFAULT_SUMMARIZER_TEMPLATE_HTML = "Answer the user's questions based on the content of the web page or the content of the document.\n" +
            "Content: ```\n" +
            "{new_lines}" +
            "```\n" +
            "\n" +
            "Questions: ```\n" +
            "{query}\n" +
            "```\n" +
            "Follow these steps to answer your question\n" +
            "1. Remove content that has no impact on the answer and is not important, such as ads, messages, chats, etc!\n" +
            "2. Results are limited to {max_tokens} characters!\n" +
            "3. Use 中文 or English to answer based on the language of the Questions!!\n" +
            "4. Just output the answer and nothing else!\n" +
            "\n" +
            "Answer:";

    /**
     * 读取给到的文档内容，并根据相关的问题，进行回答
     * 注意 只从内容中获取信息进行回答，不要回答你并不知道的内容
     * 直接输出结果即可
     */


    private BasePromptTemplate prompt;

    /**
     * 总结输出最大tokens
     */
    private int summaryMaxTokens = 500;


    public SummarizerMixin() {

        super();
        this.prompt = buildPromptTemplate();
    }

    public SummarizerMixin(BaseLanguageModel llm) {
        super();
        this.prompt = buildPromptTemplate();

        this.llm = llm;
    }


    /**
     * 根据tokens 自动切换LLM 生成总结
     *
     * @param content
     */
    public static BaseLLMResult summaryContentCall(String content, String query, Integer maxTokens) {

        Long start = System.currentTimeMillis();

        try {

            if (StrUtil.isBlank(content)) {
                throw new RuntimeException("summary content is empty");
            }

            if (content.length() <= 350) {
                throw new RuntimeException("summary content is <= 350");
            }


            //计算tokens
            int tokens = SummarizerMixin.calculateTokens(content);

            ChatOpenAI chatOpenAi = new ChatOpenAI();

            chatOpenAi.setMaxTokens(maxTokens);
            chatOpenAi.setTemperature(0d);

            //prompt 也增加下
            int promptTokens = tokens + SummarizerMixin.calculateTokens(DEFAULT_SUMMARIZER_TEMPLATE_HTML);

            if (promptTokens <= ModelTypeEnum.GPT_3_5_TURBO.getMaxContextLength()) {
                chatOpenAi.setModel(ModelTypeEnum.GPT_3_5_TURBO.getName());
            } else if (promptTokens <= ModelTypeEnum.GPT_3_5_TURBO_16K.getMaxContextLength()) {
                chatOpenAi.setModel(ModelTypeEnum.GPT_3_5_TURBO_16K.getName());
            } else {
                //@todo 大于最大之后 如何总结
                throw new RuntimeException("promptTokens is Exceeding the maximum value [" + promptTokens + "]");
            }

            LLMChain<BaseLLMResult> llmChain = new LLMChain(chatOpenAi, buildPromptTemplateHtml());

            BaseLLMResult llmResult = llmChain.call(Arrays.asList(
                    BaseVariable.newString("new_lines", content),
                    BaseVariable.newInt("max_tokens", maxTokens),
                    BaseVariable.newString("query", query)
            ));

            if (llmResult == null) {
                throw new RuntimeException("result is null");
            }

            Long end = System.currentTimeMillis();
            log.info("summaryContent is success, {} ms", end - start);
            return llmResult;

        } catch (Exception e) {

            log.error("summaryContent is error: {}", e.getMessage(), e);
            return null;
        }
    }

    public static int calculateTokens(String historyStr) {

        //@todo 总结也不一定看模型，还要看成本，保证比较小的tokens下进行对话，所以比较的是计算后剩余可用的tokens
        return TokenUtils.intTokens(ModelType.GPT_3_5_TURBO, historyStr);
    }

    protected static BasePromptTemplate buildPromptTemplate() {

        return new PromptTemplate(DEFAULT_SUMMARIZER_TEMPLATE, Arrays.asList(
                BaseVariable.newString("summary"),
                BaseVariable.newString("new_lines"),
                BaseVariable.newString("max_tokens")
        ));
    }

    protected static BasePromptTemplate buildPromptTemplateHtml() {

        return new PromptTemplate(DEFAULT_SUMMARIZER_TEMPLATE_HTML, Arrays.asList(
                BaseVariable.newString("new_lines"),
                BaseVariable.newString("max_tokens"),
                BaseVariable.newString("query")
        ));
    }

    @SneakyThrows
    protected BaseMessage createSummaryMessage(String text) {
        Class c = Class.forName(this.summaryMessageCls.getName());
        BaseMessage message = (BaseMessage) c.getConstructor(String.class).newInstance(text);

        return message;
    }


    protected BaseLLMResult predictNewSummary(List<BaseMessage> messages, String existingSummary, Integer maxTokens) {

        String newLines = BaseMessage.getBufferString(messages);

        LLMChain<BaseLLMResult> llmChain = new LLMChain(this.llm, this.prompt);

        return llmChain.call(Arrays.asList(
                BaseVariable.newString("new_lines", newLines),
                BaseVariable.newString("summary", existingSummary),
                BaseVariable.newInt("max_tokens", maxTokens)
        ));
    }

    protected BaseLLMResult predictNewSummary(List<BaseMessage> messages, String existingSummary) {

        return predictNewSummary(messages, existingSummary, this.getSummaryMaxTokens());
    }


}
