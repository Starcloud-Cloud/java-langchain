package com.starcloud.ops.llm.langchain.core.schema.prompt;


import com.starcloud.ops.llm.langchain.core.prompt.base.PromptValue;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import lombok.Data;

import java.io.Serializable;
import java.util.*;


/**
 * @author df007df
 */
@Data
public abstract class BasePromptTemplate implements Serializable {

    private List<BaseVariable> inputVariables;

    private Object outputParser;


    public abstract String format(List<BaseVariable> variables);

    public abstract PromptValue formatPrompt(List<BaseVariable> variables);

    public PromptValue formatPrompt(Map<String, Object> maps) {
        return this.formatPrompt(BaseVariable.fromMap(maps));
    }

    public void save() {

    }


}

