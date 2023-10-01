package com.starcloud.ops.llm.langchain.core.prompt.base.template;

import cn.hutool.core.util.ReflectUtil;
import com.starcloud.ops.llm.langchain.core.prompt.base.StringPromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import com.starcloud.ops.llm.langchain.core.schema.prompt.BasePromptTemplate;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author df007df
 */
@NoArgsConstructor
@Data
@Slf4j
public abstract class BaseStringMessagePromptTemplate extends BaseMessagePromptTemplate {

    private StringPromptTemplate promptTemplate;

    private Object additionalKwargs;

    public BaseStringMessagePromptTemplate(StringPromptTemplate promptTemplate) {
        super();
        this.promptTemplate = promptTemplate;
    }

    public abstract BaseMessage format(List<BaseVariable> variables);

    @Override
    public List<BaseMessage> formatMessages(List<BaseVariable> variables) {
        return Arrays.asList(this.format(variables));
    }
}
