package com.starcloud.ops.llm.langchain.core.model.chat.base.message;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@Deprecated
public class HumanMessage extends BaseChatMessage {

    @Builder.Default
    private String role = "user";
}
