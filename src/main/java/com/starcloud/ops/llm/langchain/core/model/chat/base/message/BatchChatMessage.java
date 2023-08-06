package com.starcloud.ops.llm.langchain.core.model.chat.base.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BatchChatMessage {

    private List<? extends BaseChatMessage> messages;
}
