package com.starcloud.ops.llm.langchain.core.agent.base;

import com.starcloud.ops.llm.langchain.core.agent.base.action.AgentAction;
import com.starcloud.ops.llm.langchain.core.agent.base.parser.AgentOutputParser;
import com.starcloud.ops.llm.langchain.core.chain.LLMChain;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.callbacks.BaseCallbackManager;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public abstract class BaseAgent extends BaseSingleActionAgent {

    private LLMChain llmChain;

    private AgentOutputParser outputParser;

    private List<String> allowedTools = new ArrayList<>();


    @Override
    public List<AgentAction> plan(List<AgentAction> intermediateSteps, List<BaseVariable> variables, BaseCallbackManager callbackManager) {
        return null;
    }

    @Override
    public List<String> inputKeys() {
        return null;
    }

    public abstract String observationPrefix();

    public abstract String llmPrefix();


    public List<String> returnValues() {

        return Arrays.asList("output");
    }

    public List<String> stop() {
        return Arrays.asList("\n" + this.observationPrefix(), "\n\t" + this.observationPrefix());
    }
}
