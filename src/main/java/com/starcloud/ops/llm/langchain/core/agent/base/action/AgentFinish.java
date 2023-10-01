package com.starcloud.ops.llm.langchain.core.agent.base.action;

import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class AgentFinish extends AgentAction {

    private Integer errorCode;

    private String error;

    /**
     * 非LLM结果触发的LLM，即未调用fun 就返回结束了
     */
    private Boolean noFunLLm;

    private List<BaseMessage> messagesLog;

    private Map<String, Object> returnValues = new HashMap<>();

    public static AgentFinish error(Integer errorCode, String error, String log) {

        Map<String, Object> params = new HashMap();
        AgentFinish agentFinish = new AgentFinish(params, log);
        agentFinish.setStatus(false);
        agentFinish.setError(error);
        agentFinish.setErrorCode(errorCode);

        return agentFinish;
    }

    public Object getOutput() {
        return this.returnValues.get("output");
    }

    public AgentFinish(Map<String, Object> returnValues, String log) {
        this.setStatus(true);
        this.returnValues = returnValues;
        this.setLog(log);
    }

    public AgentFinish(Object returnValues, String log) {
        this.setStatus(true);
        this.returnValues.put("output", returnValues);
        this.setLog(log);
    }


    public AgentFinish(Object returnValues) {
        this.setStatus(true);
        this.returnValues.put("output", returnValues);
    }

}
