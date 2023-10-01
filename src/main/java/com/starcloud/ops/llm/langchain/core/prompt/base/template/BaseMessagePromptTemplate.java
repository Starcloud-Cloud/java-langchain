package com.starcloud.ops.llm.langchain.core.prompt.base.template;

import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * @author df007df
 */
@Data
public abstract class BaseMessagePromptTemplate implements Serializable {

    public abstract List<BaseMessage> formatMessages(List<BaseVariable> variables);

}
