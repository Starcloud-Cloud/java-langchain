package com.starcloud.ops.llm.langchain.core.schema.prompt;

import cn.hutool.core.util.StrUtil;
import com.starcloud.ops.llm.langchain.core.prompt.base.PromptValue;
import com.starcloud.ops.llm.langchain.core.prompt.base.StringPromptValue;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import lombok.Data;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author df007df
 */
@Data
public abstract class BasePromptTemplate implements Serializable {

    private List<BaseVariable> inputVariables;

    private Object outputParser;

//    public BasePromptTemplate(String prompt) {
//        this.template = new StringPromptTemplate(prompt);
//    }
//
//    public BasePromptTemplate(String prompt, List<BaseVariable> inputVariables) {
//        this.template = new StringPromptTemplate(prompt);
//        this.inputVariables = inputVariables;
//    }

    public abstract String format(List<BaseVariable> variables);

    public abstract PromptValue formatPrompt(List<BaseVariable> variables);

    public PromptValue formatPrompt(Map<String, Object> maps) {
       return this.formatPrompt(BaseVariable.fromMap(maps));
    }

    public void save() {

    }

//
//    public static BasePromptTemplate of(String prompt, List<BaseVariable> inputVariables) {
//        return new BasePromptTemplate(prompt, inputVariables);
//    }
//
//    public static BasePromptTemplate of(String prompt, String... inputVariables) {
//
//        return new BasePromptTemplate(prompt, Arrays.stream(Optional.ofNullable(inputVariables).orElse(new String[]{})).map(BaseVariable::newString).collect(Collectors.toList()));
//    }
//
//    public static BasePromptTemplate of(String prompt) {
//
//        return new BasePromptTemplate(prompt);
//    }
//
//    public BaseVariable getFirstVariable() {
//        return Optional.ofNullable(this.inputVariables).orElse(new ArrayList<>()).stream().findFirst().get();
//    }

    @Deprecated
    public PromptValue formatPrompt() {

        return formatPrompt(this.inputVariables);
    }

//    public PromptValue formatPrompt(Map<String, Object> maps) {
//
//        List<BaseVariable> variables = new ArrayList<>();
//        maps.forEach((key, value) -> {
//            variables.add(BaseVariable.builder()
//                    .field(key)
//                    .value(value)
//                    .build());
//        });
//
//        return this.formatPrompt(variables);
//    }


//    public PromptValue formatPrompt(List<BaseVariable> variables) {
//        Map maps = Optional.ofNullable(variables).orElse(new ArrayList<>()).stream().collect(Collectors.toMap(BaseVariable::getField, (variable) -> {
//            return Optional.ofNullable(variable.getValue()).orElse("");
//        }));
//
//        String str = StrUtil.format(this.getTemplate().getPrompt(), maps);
//
//        return new StringPromptValue(str);
//    }


}

