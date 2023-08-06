package com.starcloud.ops.llm.langchain.core.prompt.base.template;

import com.starcloud.ops.llm.langchain.core.model.chat.base.message.BaseChatMessage;
import com.starcloud.ops.llm.langchain.core.prompt.base.PromptValue;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import com.starcloud.ops.llm.langchain.core.schema.message.BaseMessage;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * @author df007df
 */
@Data
public abstract class BaseMessagePromptTemplate implements Serializable {

//    private String role;
//
//    private BasePromptTemplate promptTemplate;
//
//    public BaseMessagePromptTemplate(BasePromptTemplate promptTemplate) {
//        this.setPromptTemplate(promptTemplate);
//    }

    //public abstract PromptValue formatPrompt(List<BaseVariable> variables);

    public abstract List<BaseMessage> formatMessages(List<BaseVariable> variables);


//
//    public BaseChatMessage formatMessages(List<BaseVariable> variables) {
//
//        PromptValue promptValue = this.formatPrompt(variables);
//
//        return BaseChatMessage.builder()
//                .role(this.getRole())
//                .content(promptValue.toStr())
//                .build();
//    }


}
