package com.starcloud.ops.llm.langchain.core.callbacks;

import com.starcloud.ops.llm.langchain.core.callbacks.mixin.*;

public interface BaseCallbackHandler extends LLMManagerMixin, ChainManagerMixin, ToolManagerMixin, RetrieverManagerMixin, CallbackManagerMixin, RunManagerMixin {

    default Boolean ignoreLLM() {
        return false;
    }

    default Boolean ignoreChain() {
        return false;
    }

}
