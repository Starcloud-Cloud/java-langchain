package com.starcloud.ops.llm.langchain.core.model.llm.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author df007df
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Data
public class BaseLLMUsage {

    private Long promptTokens;

    private Long completionTokens;

    private Long totalTokens;

}
