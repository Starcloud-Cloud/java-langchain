package com.starcloud.ops.llm.langchain.core.chain;

import com.starcloud.ops.llm.langchain.core.callbacks.CallbackManagerForChainRun;
import com.starcloud.ops.llm.langchain.core.chain.base.Chain;
import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.PromptValue;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.BaseLanguageModel;
import com.starcloud.ops.llm.langchain.core.schema.prompt.BasePromptTemplate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Slf4j
@EqualsAndHashCode(callSuper = true)
@Data
public class LLMChain<R> extends Chain<BaseLLMResult<R>> {

    private BaseLanguageModel<R> llm;

    private static final Logger logger = LoggerFactory.getLogger(LLMChain.class);

    private BasePromptTemplate promptTemplate;

    public LLMChain(BaseLanguageModel<R> llm, BasePromptTemplate promptTemplate) {
        this.setLlm(llm);
        this.setPromptTemplate(promptTemplate);
    }

    public LLMChain(BaseLanguageModel<R> llm, BasePromptTemplate promptTemplate, String outputKey) {
        this.setLlm(llm);
        this.setPromptTemplate(promptTemplate);
        this.setOutputKeys(Arrays.asList(outputKey));
    }

    @Override
    protected BaseLLMResult<R> _call(List<BaseVariable> baseVariables) {

        PromptValue promptValue = this.promptTemplate.formatPrompt(baseVariables);

        this.getLlm().setVerbose(this.getVerbose());
        return this.getLlm().generatePrompt(Arrays.asList(promptValue));
    }

    @Override
    public String run(List<BaseVariable> baseVariables) {

        return this.call(baseVariables).getText();
    }

    @Override
    public String run(String text) {

        return this.call(Arrays.asList(BaseVariable.newString("input", text))).getText();
    }
//
//    protected BaseLLMResult<R> _call(List<BaseVariable> baseVariables, CallbackManagerForChainRun chainRun) {
//        PromptValue promptValue = this.promptTemplate.formatPrompt(baseVariables);
//
//        this.getLlm().setVerbose(this.getVerbose());
//        return this.getLlm().generatePrompt(Arrays.asList(promptValue));
//    }

}
