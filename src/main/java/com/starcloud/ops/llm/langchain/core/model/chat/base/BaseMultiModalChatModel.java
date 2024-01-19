package com.starcloud.ops.llm.langchain.core.model.chat.base;

import com.starcloud.ops.llm.langchain.core.callbacks.CallbackManagerForLLMRun;
import com.starcloud.ops.llm.langchain.core.model.llm.base.ChatResult;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.multimodal.HumanMessage;
import com.starcloud.ops.llm.langchain.core.schema.tool.FunctionDescription;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;


@Slf4j
@Data
public abstract class BaseMultiModalChatModel<R> extends BaseChatModel<R> {

    private static final Logger logger = LoggerFactory.getLogger(BaseMultiModalChatModel.class);


    @Override
    public String predict(String text, List<String> stops) {

        HumanMessage message = com.starcloud.ops.llm.langchain.core.schema.message.multimodal.HumanMessage.ofTestImages(text);
        return this.call(Arrays.asList(message), stops);
    }

    protected abstract ChatResult<R> _generate(List<? extends BaseMessage> chatMessages, List<String> stops, CallbackManagerForLLMRun callbackManager);


    @Override
    protected ChatResult<R> _generate(List<BaseMessage> chatMessages, List<String> stops, List<FunctionDescription> functions, CallbackManagerForLLMRun callbackManager) {
        return this._generate(chatMessages, stops, callbackManager);
    }


}
