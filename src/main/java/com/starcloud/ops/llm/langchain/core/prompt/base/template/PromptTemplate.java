package com.starcloud.ops.llm.langchain.core.prompt.base.template;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.starcloud.ops.llm.langchain.core.prompt.base.StringPromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author df007df
 */
@Data
public class PromptTemplate extends StringPromptTemplate {

    private List<BaseVariable> inputVariables;

    private String template;

    private String templateFormat = "f-string";

    public PromptTemplate(String template) {
        this.template = template;
    }

    public PromptTemplate(String template, List<BaseVariable> inputVariables) {
        this.inputVariables = inputVariables;
        this.template = template;
    }


    @Override
    public String format(List<BaseVariable> variables) {

        Map<String, Object> allVariablesValues = MapUtil.newHashMap();

        Optional.ofNullable(variables).orElse(new ArrayList<>()).forEach(variable -> {

            allVariablesValues.put(variable.getField(), variable.getValue());
        });

        return StrUtil.format(this.template, allVariablesValues);
    }

    public static PromptTemplate fromTemplate(String... params) {

        Assert.notEmpty(params);

        String tmp = Arrays.stream(params).findFirst().get();
        List<BaseVariable> variables = Arrays.stream(Arrays.stream(params).skip(1).toArray()).map(str -> BaseVariable.newString((String) str)).collect(Collectors.toList());
        return fromTemplate(tmp, variables);
    }

    public static PromptTemplate fromTemplate(String template, List<BaseVariable> variables) {
        return new PromptTemplate(template, variables);
    }
}
