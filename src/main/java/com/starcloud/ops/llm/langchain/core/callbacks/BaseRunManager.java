package com.starcloud.ops.llm.langchain.core.callbacks;

import com.starcloud.ops.llm.langchain.core.callbacks.mixin.RunManagerMixin;
import lombok.Data;

import java.util.List;

@Data
public abstract class BaseRunManager implements RunManagerMixin {

    private String runId;

    private String parentRunId;

    private List<String> tags;

    private List<String> inheritableTags;

    private List<BaseCallbackHandler> handlers;

    public List<BaseCallbackHandler> inheritableHandlers;


    @Override
    public void onText(Object... objects) {

    }


}
