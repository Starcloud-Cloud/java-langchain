package com.starcloud.ops.llm.langchain.core.model.llm;

import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMUsage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LLMUtils {


    public static BaseLLMUsage combineBaseLLMUsage(List<BaseLLMUsage> baseLLMUsageList) {

        return Optional.ofNullable(baseLLMUsageList).orElse(new ArrayList<>()).stream().reduce((usage1, usage2) -> {
            usage1.setPromptTokens(usage1.getPromptTokens() + usage2.getPromptTokens());
            usage1.setCompletionTokens(usage1.getCompletionTokens() + usage2.getCompletionTokens());
            usage1.setTotalTokens(usage1.getTotalTokens() + usage2.getTotalTokens());

            return usage1;
        }).orElse(BaseLLMUsage.builder().build());
    }


}
