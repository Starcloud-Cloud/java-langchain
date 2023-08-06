package com.starcloud.ops.llm.langchain.core.schema.parser;

import lombok.Data;

@Data
public class OutputParserException extends RuntimeException {

    Object error;

    String observation;

    String llmOutput;

    Boolean sendToLLM;


}
