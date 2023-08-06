package com.starcloud.ops.llm.langchain.core.indexes.splitter;

import com.knuddels.jtokkit.api.ModelType;
import com.starcloud.ops.llm.langchain.core.utils.TokenUtils;


public class TokenTextSplitter extends BasicTextSplitter {

    @Override
    Long lengthFunction(String text) {
        return TokenUtils.tokens(ModelType.TEXT_DAVINCI_002, text);
    }
}
