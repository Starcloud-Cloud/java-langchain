package com.starcloud.ops.llm.langchain.core.callbacks;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
public abstract class BaseCallbackManager implements BaseCallbackHandler {

    private List<BaseCallbackHandler> handlers = new ArrayList<>();

    private List<BaseCallbackHandler> inheritableHandlers;

    private String parentRunId;

    private List<String> tags;

    public BaseCallbackManager addCallbackHandler(BaseCallbackHandler callbackHandler) {
        this.getHandlers().add(callbackHandler);
        return this;
    }

    public BaseCallbackManager setCallbackHandler(List<BaseCallbackHandler> callbackHandlerList) {
        this.handlers = callbackHandlerList;
        return this;
    }

    public BaseCallbackManager removeCallbackHandler(BaseCallbackHandler callbackHandler) {

        this.getHandlers().remove(callbackHandler);
        return this;
    }

    public void addTag(List<String> tags) {
        this.tags.addAll(tags);
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }


}
