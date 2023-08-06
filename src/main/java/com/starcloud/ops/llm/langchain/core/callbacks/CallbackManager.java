package com.starcloud.ops.llm.langchain.core.callbacks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CallbackManager extends BaseCallbackManager {


    @Override
    public List<CallbackManagerForLLMRun> onLLMStart(Object... objects) {

        CallbackManagerForLLMRun llmRun = new CallbackManagerForLLMRun();

        llmRun.setHandlers(this.getHandlers());

        Optional.ofNullable(this.getHandlers()).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onLLMStart(objects);
        }));

        return Arrays.asList(llmRun);
    }

    @Override
    public List<CallbackManagerForLLMRun> onChatModelStart(Object... objects) {


        CallbackManagerForLLMRun llmRun = new CallbackManagerForLLMRun();

        llmRun.setHandlers(this.getHandlers());

        Optional.ofNullable(this.getHandlers()).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onChatModelStart(objects);
        }));


        return Arrays.asList(llmRun);

    }

    @Override
    public CallbackManagerForChainRun onChainStart(Object... objects) {

        CallbackManagerForChainRun chainRun = new CallbackManagerForChainRun();

        chainRun.setHandlers(this.getHandlers());

        Optional.ofNullable(this.getHandlers()).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onChainStart(objects);
        }));

        return chainRun;

    }

    @Override
    public void onChainError(String message, Throwable throwable) {
        Optional.ofNullable(this.getHandlers()).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onChainError(message, throwable);
        }));
    }


    @Override
    public CallbackManagerForToolRun onToolStart(Object... objects) {

        CallbackManagerForToolRun toolRun = new CallbackManagerForToolRun();

        toolRun.setHandlers(this.getHandlers());


        Optional.ofNullable(this.getHandlers()).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onToolStart(objects);
        }));

        return toolRun;
    }

}
