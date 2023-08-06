package com.starcloud.ops.llm.langchain.core.agent.base.action;

import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import lombok.Data;

import java.util.List;

@Data
public class FunctionsAgentAction extends AgentAction {

    private List<BaseMessage> messagesLog;

    public FunctionsAgentAction(List<BaseMessage> messagesLog) {
        this.messagesLog = messagesLog;
    }

    public FunctionsAgentAction(String tool, Object toolInput, String log, List<BaseMessage> messagesLog) {

        this.setTool(tool);
        this.setToolInput(toolInput);
        this.setLog(log);
        this.messagesLog = messagesLog;
    }
}
