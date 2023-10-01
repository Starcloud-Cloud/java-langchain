package com.starcloud.ops.llm.langchain.core.prompt.base.template;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.starcloud.ops.llm.langchain.core.prompt.base.PromptValue;
import com.starcloud.ops.llm.langchain.core.prompt.base.StringPromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.StringPromptValue;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import com.starcloud.ops.llm.langchain.core.schema.prompt.BasePromptTemplate;
import lombok.Data;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author df007df
 */
@Data
public class PromptTemplate extends StringPromptTemplate {

    private List<BaseVariable> inputVariables;

    private String template;

    /**
     * 动态生成模版
     */
    private Supplier<String> templateSupplier;

    public PromptTemplate(String template) {
        this.template = template;
    }

    public PromptTemplate(String template, List<BaseVariable> inputVariables) {
        this.setInputVariables(inputVariables);
        this.template = template;
    }


    @Override
    public String format(List<BaseVariable> variables) {

        Map<String, Object> allVariablesValues = MapUtil.newHashMap();

        //合并变量
        variables = this.mergeVariable(variables);

        Optional.ofNullable(variables).orElse(new ArrayList<>()).forEach(variable -> {

            if (BaseVariable.VariableTypeEnum.SUPPLIER.equals(variable.getType())) {
                allVariablesValues.put(variable.getField(), ((Supplier<?>) variable.getValue()).get());
            } else if (BaseVariable.VariableTypeEnum.TEMPLATE.equals(variable.getType())) {
                allVariablesValues.put(variable.getField(), ((BasePromptTemplate) variable.getValue()).formatPrompt().toStr());
            } else {
                allVariablesValues.put(variable.getField(), variable.getValue());
            }

        });

        String tmp = "";
        if (this.getTemplateSupplier() != null) {
            tmp = this.getTemplateSupplier().get();
        } else {
            tmp = this.template;
        }

        return StrUtil.format(tmp, allVariablesValues, false);
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

    public static PromptTemplate fromTemplate(Supplier<String> templateSupplier, List<BaseVariable> variables) {

        PromptTemplate promptTemplate = new PromptTemplate("", variables);
        promptTemplate.setTemplateSupplier(templateSupplier);
        return promptTemplate;
    }
}
