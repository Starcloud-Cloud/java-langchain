package com.starcloud.ops.llm.langchain.core.agent.base.parser;

import com.starcloud.ops.llm.langchain.core.agent.base.action.AgentAction;

public abstract class AgentOutputParser extends BaseOutputParser<AgentAction> {

    /**
     * Parse text into agent action/finish
     * @param text
     * @return
     */
    @Override
    public abstract AgentAction parse(String text);

}
