package com.starcloud.ops.llm.langchain.core.schema.parser;

import lombok.Data;

@Data
public class OutputParserException extends RuntimeException {


    String observation;

    Object llmOutput;

    Boolean sendToLLM;


    public OutputParserException(String message) {
        super(message);
    }
}
