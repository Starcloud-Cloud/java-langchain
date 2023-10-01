package com.starcloud.ops.llm.langchain.core.agent.base;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.starcloud.ops.llm.langchain.core.agent.base.action.AgentAction;
import com.starcloud.ops.llm.langchain.core.agent.base.action.AgentFinish;
import com.starcloud.ops.llm.langchain.core.agent.base.action.FunctionsAgentAction;
import com.starcloud.ops.llm.langchain.core.memory.BaseChatMemory;
import com.starcloud.ops.llm.langchain.core.memory.BaseMemory;
import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.callbacks.BaseCallbackManager;
import com.starcloud.ops.llm.langchain.core.schema.message.AIMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import com.starcloud.ops.llm.langchain.core.schema.message.FunctionMessage;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

@Data
public abstract class BaseSingleActionAgent {

    public static final String TEMP_VARIABLE_SCRATCHPAD = "agent_scratchpad";

    private AgentExecutor agentExecutor;

    private List<String> returnValues;

    public List<String> getAllowedTools() {
        return new ArrayList<>();
    }

    public abstract List<AgentAction> plan(List<AgentAction> intermediateSteps, List<BaseVariable> variables, BaseCallbackManager callbackManager);

    public AgentFinish returnStoppedResponse(String earlyStoppingMethod, List<AgentAction> intermediateSteps, int iterations, long timeElapsed) {

        Assert.equals("force", earlyStoppingMethod, "Got unsupported early_stopping_method " + earlyStoppingMethod);

        String log = Optional.ofNullable(intermediateSteps).orElse(new ArrayList<>()).stream().map(AgentAction::getLog).collect(Collectors.joining("\n"));

        return AgentFinish.error(-3, "Agent stopped due to iteration limit[" + iterations + "] or time[" + timeElapsed + "] limit.", log);
    }
//
//    public static BaseSingleActionAgent fromLLMAndTools(BaseLanguageModel llm, List<BaseTool> tools, BaseCallbackManager callbackManager, List<BaseMessagePromptTemplate> extraPromptMessages, SystemMessage systemMessage) {
//        return null;
//    }

    public abstract List<String> inputKeys();

    public String agentType() {
        return this.getClass().getSimpleName();
    }

    public void save() {
        return;

    }

    protected Map<String, Object> toolRunLoggingKwargs() {
        return MapUtil.newHashMap();
    }
}
