package com.starcloud.ops.llm.langchain.core.model.llm.document;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SplitDetail {

    private String segment;

    private Long tokens;

}
