package com.starcloud.ops.llm.langchain.core.callbacks;

import com.starcloud.ops.llm.langchain.core.callbacks.mixin.ChainManagerMixin;

import java.util.ArrayList;
import java.util.Optional;


public class CallbackManagerForLLMRun extends BaseRunManager implements ChainManagerMixin {


    public void onLLMStart(Object... objects) {

        Optional.ofNullable(this.getHandlers()).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onLLMStart(objects);
        }));
    }


    public void onLLMNewToken(Object... objects) {
        Optional.ofNullable(this.getHandlers()).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onLLMNewToken(objects);
        }));
    }

    public void onLLMEnd(Object... objects) {

        Optional.ofNullable(this.getHandlers()).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onLLMEnd(objects);
        }));
    }

    public void onLLMError(String message) {

        Optional.ofNullable(this.getHandlers()).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onLLMError(message);
        }));
    }

    public void onLLMError(String message, Throwable throwable) {

        Optional.ofNullable(this.getHandlers()).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onLLMError(message, throwable);
        }));
    }
}
