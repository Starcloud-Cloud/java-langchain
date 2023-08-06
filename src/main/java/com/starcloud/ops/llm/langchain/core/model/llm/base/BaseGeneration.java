package com.starcloud.ops.llm.langchain.core.model.llm.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BaseGeneration<R> implements Serializable {

    private String text;

    private R generationInfo;

}
