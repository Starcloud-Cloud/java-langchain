package com.starcloud.ops.llm.langchain.core.callbacks;


import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@Slf4j
public class LLMCallbackManager extends BaseCallbackManager {

    private List<BaseCallbackHandler> callbackHandlerList = new ArrayList<BaseCallbackHandler>() {{
        add(new StdOutCallbackHandler());
    }};

//    @Override
//    public LLMCallbackManager addCallbackHandler(BaseCallbackHandler callbackHandler) {
//        this.callbackHandlerList.add(callbackHandler);
//        return this;
//    }

//    @Override
//    public LLMCallbackManager removeCallbackHandler(BaseCallbackHandler callbackHandler) {
//        this.callbackHandlerList.remove(callbackHandler);
//        return this;
//    }

    public LLMCallbackManager(List<BaseCallbackHandler> callbackHandlerList) {
        this.callbackHandlerList = callbackHandlerList;
    }

    @Override
    public Void onChainStart(Object... objects) {

        Optional.ofNullable(callbackHandlerList).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onChainStart(objects);
        }));

        return null;
    }

    public void onChainEnd(Object... objects) {

        Optional.ofNullable(callbackHandlerList).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onChainEnd(objects);
        }));
    }

    public void onChainError(String message, Throwable throwable) {

        Optional.ofNullable(callbackHandlerList).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onChainError(message, throwable);
        }));
    }

    @Override
    public List<CallbackManagerForLLMRun> onChatModelStart(Object... objects) {

        List<CallbackManagerForLLMRun> llmRuns = new ArrayList<>();
        Optional.ofNullable(callbackHandlerList).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onChatModelStart(objects);
        }));


        llmRuns.add(new CallbackManagerForLLMRun());

        return llmRuns;

    }

    @Override
    public Void onLLMStart(Object... objects) {

        Optional.ofNullable(callbackHandlerList).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onLLMStart(objects);
        }));

        return null;
    }


    @Override
    public CallbackManagerForToolRun onToolStart(Object... objects) {

        Optional.ofNullable(callbackHandlerList).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onToolStart(objects);
        }));


        return new CallbackManagerForToolRun();

    }


}
