package com.starcloud.ops.llm.langchain.core.agent.base.action;


import com.starcloud.ops.llm.langchain.core.utils.JsonUtils;
import lombok.Data;

@Data
public abstract class AgentAction {

    /**
     * 执行状态
     */
    private Boolean status;

    private String log;

    private Object observation;

    private Object response;

    public String getObservation() {

        if (this.observation instanceof Number || this.observation instanceof String) {
            return String.valueOf(this.observation);
        } else {
            return JsonUtils.toJsonString(this.observation);
        }
    }
}
