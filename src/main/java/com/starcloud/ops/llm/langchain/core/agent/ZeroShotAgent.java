package com.starcloud.ops.llm.langchain.core.agent;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.starcloud.ops.llm.langchain.core.agent.base.BaseAgent;
import com.starcloud.ops.llm.langchain.core.agent.base.parser.AgentOutputParser;
import com.starcloud.ops.llm.langchain.core.agent.base.parser.MRKLOutputParser;
import com.starcloud.ops.llm.langchain.core.callbacks.BaseCallbackManager;
import com.starcloud.ops.llm.langchain.core.callbacks.CallbackManager;
import com.starcloud.ops.llm.langchain.core.callbacks.StdOutCallbackHandler;
import com.starcloud.ops.llm.langchain.core.model.chat.ChatOpenAI;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.BaseMessagePromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.PromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.BaseLanguageModel;
import com.starcloud.ops.llm.langchain.core.schema.message.SystemMessage;
import com.starcloud.ops.llm.langchain.core.tools.base.BaseTool;
import lombok.Data;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
public class ZeroShotAgent extends BaseAgent {

    private static final String PREFIX = "Answer the following questions as best you can. You have access to the following tools:";

    private static final String SUFFIX = "Begin!\n" +
            "\n" +
            "Question: {input}\n" +
            "Thought:{agent_scratchpad}";


    private static final String FORMAT_INSTRUCTIONS = "Use the following format:\n" +
            "\n" +
            "Question: the input question you must answer\n" +
            "Thought: you should always think about what to do\n" +
            "Action: the action to take, should be one of [{tool_names}]\n" +
            "Action Input: the input to the action\n" +
            "Observation: the result of the action\n" +
            "... (this Thought/Action/Action Input/Observation can repeat N times)\n" +
            "Thought: I now know the final answer\n" +
            "Final Answer: the final answer to the original input question";


    public AgentOutputParser getDefaultOutputParser() {

        return new MRKLOutputParser();
    }


    @Override
    public String observationPrefix() {
        return "Observation: ";
    }

    @Override
    public String llmPrefix() {
        return "Thought:";
    }


    public static PromptTemplate createPrompt(List<BaseTool> tools, String prefix, String suffix, String formatInstructions, List<String> inputVariables) {


        prefix = Optional.ofNullable(prefix).orElse(PREFIX);
        suffix = Optional.ofNullable(suffix).orElse(SUFFIX);
        formatInstructions = Optional.ofNullable(formatInstructions).orElse(FORMAT_INSTRUCTIONS);

        String toolStrings = Optional.ofNullable(tools).orElse(new ArrayList<>()).stream().map(tool -> {
            return StrUtil.format("{}: {}", tool.getName(), tool.getDescription());
        }).collect(Collectors.joining("\n"));

        String toolNames = Optional.ofNullable(tools).orElse(new ArrayList<>()).stream().map(tool -> {
            return tool.getName();
        }).collect(Collectors.joining(", "));

        formatInstructions = StrUtil.format(formatInstructions, new HashMap() {{
            put("tool_names", toolNames);
        }});

        String template = StrUtil.format("{}\n\n{}\n\n{}\n\n{}", prefix, toolStrings, formatInstructions, suffix);

        inputVariables = Optional.ofNullable(inputVariables).orElse(new ArrayList() {{
            add("input");
            add("agent_scratchpad");
        }});

        List<BaseVariable> variables = Optional.ofNullable(inputVariables).orElse(new ArrayList<>()).stream().map(BaseVariable::newString).collect(Collectors.toList());

        return new PromptTemplate(template, variables);
    }

    public static OpenAIFunctionsAgent fromLLMAndTools(BaseLanguageModel llm, List<BaseTool> tools, BaseCallbackManager callbackManager, AgentOutputParser agentOutputParser, String prefix, String suffix, String formatInstructions, List<String> inputVariables) {


        return null;
    }

}
