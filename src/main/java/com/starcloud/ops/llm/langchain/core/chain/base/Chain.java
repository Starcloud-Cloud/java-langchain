package com.starcloud.ops.llm.langchain.core.chain.base;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.starcloud.ops.llm.langchain.core.callbacks.BaseCallbackManager;
import com.starcloud.ops.llm.langchain.core.callbacks.CallbackManager;
import com.starcloud.ops.llm.langchain.core.callbacks.CallbackManagerForChainRun;
import com.starcloud.ops.llm.langchain.core.memory.BaseMemory;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import lombok.Data;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;


/**
 * @author df007df
 */
@Data
public abstract class Chain<R> {

    private BaseMemory memory;

    private BaseCallbackManager callbackManager = new CallbackManager();

    public BaseCallbackManager getCallbackManager() {
        return callbackManager;
    }

    public void setCallbackManager(CallbackManager callbackManager) {
        this.callbackManager = callbackManager;
    }

    private Boolean verbose = false;

    /**
     * 输入的字段
     */
    private List<String> inputKeys;

    /**
     * 输出的字段
     */
    private List<String> outputKeys;


    public List<String> getInputKeys() {
        return this.inputKeys;
    }


    public List<String> getOutputKeys() {
        return this.outputKeys;
    }

    /**
     * 当Chain输出为Map时定义的key
     *
     * @return
     */
    public String getReturnOutputKey() {
        return this.getOutputKeys().get(0);
    }

    public Boolean getVerbose() {
        return verbose;
    }

    public void setVerbose(Boolean verbose) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.getLogger(Chain.class).setLevel(Level.DEBUG);
        this.verbose = verbose;
    }

    protected abstract R _call(List<BaseVariable> baseVariables);


    public void _validateInputs(List<BaseVariable> baseVariables) {

    }

    public void _validateOutputs(R result) {

    }

    protected List<BaseVariable> prepInputs(List<BaseVariable> baseVariables) {

        if (this.getMemory() != null) {
            List<BaseVariable> variables = this.getMemory().loadMemoryVariables();
            List<BaseVariable> variableList = Optional.ofNullable(baseVariables).orElse(new ArrayList<>()).stream().map(BaseVariable::copy).collect(Collectors.toList());
            variableList.addAll(variables);
            baseVariables = variableList;
        }

        this._validateInputs(baseVariables);

        return baseVariables;
    }

    public R call(List<BaseVariable> baseVariables) {

        baseVariables = this.prepInputs(baseVariables);

        CallbackManagerForChainRun chainRun = this.getCallbackManager().onChainStart(this.getClass(), baseVariables, this.verbose);

        R result = null;

        try {

            result = this._call(baseVariables);

        } catch (Exception e) {

            chainRun.onChainError(e.getMessage(), e);

            //this.getCallbackManager().onChainError(e.getMessage(), e);
        }

        chainRun.onChainEnd(this.getClass(), result);

        this.prepOutputs(baseVariables, result);

        return result;
    }

    protected R prepOutputs(List<BaseVariable> baseVariables, R result) {

        this._validateOutputs(result);

        if (this.getMemory() != null) {
            this.getMemory().saveContext(baseVariables, result);
        }
        return result;
    }


    public R call(Map<String, Object> maps) {

        List<BaseVariable> variables = new ArrayList<>();
        maps.forEach((key, value) -> {
            variables.add(BaseVariable.builder()
                    .field(key)
                    .value(value)
                    .build());
        });

        return this.call(variables);
    }

    public abstract String run(List<BaseVariable> baseVariables);

    public abstract String run(String text);

    public void save() {
        return;
    }

}
