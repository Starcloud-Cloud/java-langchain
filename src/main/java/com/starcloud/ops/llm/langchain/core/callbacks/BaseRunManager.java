package com.starcloud.ops.llm.langchain.core.callbacks;

import cn.hutool.core.util.ClassUtil;
import com.starcloud.ops.llm.langchain.core.callbacks.mixin.RunManagerMixin;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public Boolean hasHandler(Class<?> cls) {

        return Optional.ofNullable(this.getHandlers()).orElse(new ArrayList<>()).stream().anyMatch(handler -> {
            return ClassUtil.isAssignable(cls, handler.getClass());
        });

    }


}
