package com.starcloud.ops.llm.langchain.core.memory;

import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import lombok.Data;

import java.util.List;

@Data
public abstract class BaseMemory<R> {

    public abstract List<BaseVariable> loadMemoryVariables();

    public abstract void saveContext(List<BaseVariable> baseVariables, R result);
}
