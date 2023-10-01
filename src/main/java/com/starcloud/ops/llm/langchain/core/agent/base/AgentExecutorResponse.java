package com.starcloud.ops.llm.langchain.core.agent.base;

import com.starcloud.ops.llm.langchain.core.agent.base.action.AgentAction;
import lombok.Data;

import java.util.List;


@Data
public class AgentExecutorResponse {

    private Object output;

    private List<AgentAction> intermediateSteps;

}
