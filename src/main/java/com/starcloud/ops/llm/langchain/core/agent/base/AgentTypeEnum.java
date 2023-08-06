package com.starcloud.ops.llm.langchain.core.agent.base;

import com.starcloud.ops.llm.langchain.core.agent.OpenAIFunctionsAgent;

/**
 *
 */
public enum AgentTypeEnum {

    ZERO_SHOT_REACT_DESCRIPTION("zero-shot-react-description", null),

    REACT_DOCSTORE("react-docstore", null),

    SELF_ASK_WITH_SEARCH("self-ask-with-search", null),

    CONVERSATIONAL_REACT_DESCRIPTION("conversational-react-description", null),

    CHAT_ZERO_SHOT_REACT_DESCRIPTION("chat-zero-shot-react-description", null),

    CHAT_CONVERSATIONAL_REACT_DESCRIPTION("chat-conversational-react-description", null),

    STRUCTURED_CHAT_ZERO_SHOT_REACT_DESCRIPTION("structured-chat-zero-shot-react-description", null),

    OPENAI_FUNCTIONS("openai-functions", OpenAIFunctionsAgent.class),

    OPENAI_MULTI_FUNCTIONS("openai-multi-functions", null);

    private String code;

    public Class<? extends BaseSingleActionAgent> getAgentCls() {
        return agentCls;
    }

    AgentTypeEnum(String code) {
        this.code = code;
    }

    private Class<? extends BaseSingleActionAgent> agentCls;

    AgentTypeEnum(String code, Class<? extends BaseSingleActionAgent> cls) {
        this.code = code;
        this.agentCls = cls;
    }


}
