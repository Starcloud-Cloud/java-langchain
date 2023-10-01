package com.starcloud.ops.llm.langchain.core.memory;

import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMResult;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Data
public abstract class BaseChatMemory extends BaseMemory<BaseLLMResult> {

    private Boolean returnMessages = false;

    protected static final String MEMORY_KEY = "history";

    protected static final String INPUT_KEY = "input";

    private ChatMessageHistory chatHistory;

    public BaseChatMemory() {
        this.setChatHistory(new ChatMessageHistory());
    }

    protected BaseVariable getPromptInputKey(List<BaseVariable> baseVariables) {

        return Optional.ofNullable(baseVariables).orElse(new ArrayList<>()).stream().filter(variable -> INPUT_KEY.equals(variable.getField())).findFirst().get();
    }

    protected BaseVariable getOutputKey(List<BaseVariable> baseVariables) {

        return Optional.ofNullable(baseVariables).orElse(new ArrayList<>()).stream().filter(variable -> INPUT_KEY.equals(variable.getField())).findFirst().get();
    }


    @Override
    public void saveContext(List<BaseVariable> baseVariables, BaseLLMResult result) {

        BaseVariable variable = getPromptInputKey(baseVariables);
        getChatHistory().addUserMessage(String.valueOf(variable.getValue()));
        getChatHistory().addAiMessage(result.getText());
    }


    public void saveContext(BaseVariable input, BaseVariable output) {

        this.saveContext(Arrays.asList(input), BaseLLMResult.data(String.valueOf(output.getValue())));
    }

}
