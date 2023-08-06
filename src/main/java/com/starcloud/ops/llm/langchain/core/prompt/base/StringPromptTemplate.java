package com.starcloud.ops.llm.langchain.core.prompt.base;

import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.prompt.BasePromptTemplate;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public abstract class StringPromptTemplate extends BasePromptTemplate {

//    @Override
//    public String format(List<BaseVariable> variables) {
//        return this.formatPrompt(variables).toStr();
//    }

    @Override
    public PromptValue formatPrompt(List<BaseVariable> variables) {

        return new StringPromptValue(this.format(variables));
    }

}
