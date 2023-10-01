package com.starcloud.ops.llm.langchain.core.prompt.base.variable;

import com.starcloud.ops.llm.langchain.core.prompt.base.template.PromptTemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BaseVariable {

    private VariableTypeEnum type;

    private String field;

    private Object defaultValue;

    private Object value;

    private Map<String, String> options;

    public static BaseVariable newObject(String field, Object value) {
        return BaseVariable.builder().field(field).value(value).build();
    }

    public static BaseVariable newTemplate(String field, PromptTemplate template) {
        return BaseVariable.builder().type(VariableTypeEnum.TEMPLATE).field(field).value(template).build();
    }


    public static BaseVariable newString(String field, String value) {
        return BaseVariable.builder().type(VariableTypeEnum.STR).field(field).value(value).build();
    }

    public static BaseVariable newString(String field) {
        return BaseVariable.builder().type(VariableTypeEnum.STR).field(field).build();
    }

    public static BaseVariable newBoolean(String field) {
        return BaseVariable.builder().type(VariableTypeEnum.BOOLEAN).field(field).build();
    }

    public static BaseVariable newInt(String field, Integer value) {
        return BaseVariable.builder().type(VariableTypeEnum.INT).field(field).value(value).build();
    }

    public static BaseVariable newArray(String field) {
        return BaseVariable.builder().type(VariableTypeEnum.ARRAY).field(field).build();
    }

    public static <T> BaseVariable newFun(String field, Supplier<T> supplier) {
        return BaseVariable.builder().type(VariableTypeEnum.SUPPLIER).field(field).value(supplier).build();
    }

    public static List<BaseVariable> fromMap(Map<String, Object> maps) {

        List<BaseVariable> all = new ArrayList<>();
        for (Map.Entry entry : maps.entrySet()) {
            all.add(BaseVariable.builder().field(entry.getKey().toString()).value(entry.getValue()).build());
        }
        return all;
    }

    public static BaseVariable copy(BaseVariable variable) {

        return new BaseVariable(
                variable.getType(),
                variable.getField(),
                variable.getDefaultValue(),
                variable.getValue(),
                variable.getOptions()
        );
    }

    public static List<BaseVariable> copy(List<BaseVariable> variables) {
        return Optional.ofNullable(variables).orElse(new ArrayList<>()).stream().map(BaseVariable::copy).collect(Collectors.toList());
    }


    public static BaseVariable findVariable(List<BaseVariable> baseVariables, String field) {

        return Optional.ofNullable(baseVariables).orElse(new ArrayList<>()).stream().filter(variable -> field.equals(variable.getField())).findFirst().orElse(null);
    }

    /**
     * type
     */
    public enum VariableTypeEnum {

        INT,

        STR,

        ARRAY,

        BOOLEAN,

        SUPPLIER,

        TEMPLATE;
    }
}
