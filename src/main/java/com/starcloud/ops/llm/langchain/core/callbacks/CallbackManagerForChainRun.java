package com.starcloud.ops.llm.langchain.core.callbacks;

import com.starcloud.ops.llm.langchain.core.callbacks.mixin.ChainManagerMixin;

import java.util.ArrayList;
import java.util.Optional;

public class CallbackManagerForChainRun extends BaseRunManager implements ChainManagerMixin {

    public CallbackManager getChild() {

        CallbackManager callbackManager = new CallbackManager();

        callbackManager.setCallbackHandler(this.getInheritableHandlers());
        callbackManager.addTag(this.getInheritableTags());
        callbackManager.addTag(this.getTags());

        return callbackManager;
    }

    @Override
    public void onChainEnd(Object... objects) {

        Optional.ofNullable(this.getHandlers()).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onChainEnd(objects);
        }));
    }


    @Override
    public void onChainError(String message, Throwable throwable) {
        Optional.ofNullable(this.getHandlers()).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onChainError(message, throwable);
        }));
    }


    @Override
    public void onAgentAction(Object... objects) {

        Optional.ofNullable(this.getHandlers()).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onAgentAction(objects);
        }));
    }

    @Override
    public void onAgentFinish(Object... objects) {

        Optional.ofNullable(this.getHandlers()).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onAgentFinish(objects);
        }));
    }

}
