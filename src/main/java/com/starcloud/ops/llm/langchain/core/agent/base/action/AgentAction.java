package com.starcloud.ops.llm.langchain.core.agent.base.action;

import com.starcloud.ops.llm.langchain.core.model.llm.base.BaseLLMUsage;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public abstract class AgentAction {

    /**
     * 执行状态
     */
    private Boolean status;

    private String log;

    private Object observation;

    private Object response;
}
