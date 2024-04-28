package com.starcloud.ops.llm.langchain.core.schema;

import com.knuddels.jtokkit.api.EncodingType;
import com.knuddels.jtokkit.api.ModelType;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * LLM枚举
 */
public enum ModelTypeEnum {

    GPT_4_TURBO("gpt-4-turbo", EncodingType.CL100K_BASE, 128000),
    GPT_4("gpt-4", EncodingType.CL100K_BASE, 8192),
    GPT_4_32K("gpt-4-32k", EncodingType.CL100K_BASE, 32768),

    GPT_3_5_TURBO("gpt-3.5-turbo", EncodingType.CL100K_BASE, 16385),
    GPT_3_5_TURBO_16K("gpt-3.5-turbo", EncodingType.CL100K_BASE, 16385),

    TEXT_EMBEDDING_ADA_002("text-embedding-ada-002", EncodingType.CL100K_BASE, 8191),
    TEXT_DAVINCI_003("text-davinci-003", EncodingType.P50K_BASE, 4097),

    //@todo 先加上，但是EncodingType 不确定
    TEXT_EMBEDDING_3_SMALL("text-embedding-3-small", EncodingType.CL100K_BASE, 8191),
    TEXT_EMBEDDING_3_LARGE("text-embedding-3-large", EncodingType.CL100K_BASE, 8191),

    //不知道最大Tokens多少，随便写的
    QWEN("qwen-turbo", EncodingType.CL100K_BASE, 8192);

    private static final Map<String, ModelTypeEnum> nameToModelType = (Map) Arrays.stream(values()).collect(Collectors.toMap(ModelTypeEnum::getName, Function.identity()));
    private final String name;
    private final EncodingType encodingType;
    private final int maxContextLength;

    ModelTypeEnum(String name, EncodingType encodingType, int maxContextLength) {
        this.name = name;
        this.encodingType = encodingType;
        this.maxContextLength = maxContextLength;
    }

    public String getName() {
        return this.name;
    }

    public EncodingType getEncodingType() {
        return this.encodingType;
    }

    public int getMaxContextLength() {
        return this.maxContextLength;
    }

    public static Optional<ModelTypeEnum> fromName(String name) {
        return Optional.ofNullable(nameToModelType.get(name));
    }
}
