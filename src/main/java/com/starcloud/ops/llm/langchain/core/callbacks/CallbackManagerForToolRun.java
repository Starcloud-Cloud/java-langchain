package com.starcloud.ops.llm.langchain.core.callbacks;

import com.starcloud.ops.llm.langchain.core.callbacks.mixin.ToolManagerMixin;

import java.util.ArrayList;
import java.util.Optional;

public class CallbackManagerForToolRun extends BaseRunManager implements ToolManagerMixin {

    public CallbackManager getChild() {

        CallbackManager callbackManager = new CallbackManager();

        callbackManager.setCallbackHandler(this.getInheritableHandlers());
        callbackManager.addTag(this.getInheritableTags());
        callbackManager.addTag(this.getTags());

        return callbackManager;
    }


    @Override
    public void onToolEnd(Object... objects) {

        Optional.ofNullable(this.getHandlers()).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onToolEnd(objects);
        }));
    }


    @Override
    public void onToolError(String message, Throwable throwable) {

        Optional.ofNullable(this.getHandlers()).orElse(new ArrayList<>()).forEach((callbackHandler -> {
            callbackHandler.onToolError(message, throwable);
        }));
    }
}
