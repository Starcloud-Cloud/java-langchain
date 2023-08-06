package com.starcloud.ops.llm.langchain.core.utils;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.ModelType;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.ToIntFunction;


public class TokenUtils {
    private static final int REPLY_PRIMED_NUM = 3;
    private static final EncodingRegistry REGISTRY = Encodings.newDefaultEncodingRegistry();
    private static final Map<String, ToIntFunction<String>> PER_MODEL_MAP = new HashMap<>(4);

    static {
        PER_MODEL_MAP.put("gpt-3.5-turbo", name -> 4 + (StringUtils.hasText(name) ? -1 : 0));
        PER_MODEL_MAP.put("gpt-3.5-turbo-0301", name -> 4 + (StringUtils.hasText(name) ? -1 : 0));
        PER_MODEL_MAP.put("gpt-4", name -> 3 + (StringUtils.hasText(name) ? 1 : 0));
        PER_MODEL_MAP.put("gpt-4-0314", name -> 3 + (StringUtils.hasText(name) ? 1 : 0));
    }

    /**
     * Get the {@link #REGISTRY}.
     *
     * @return {@link EncodingRegistry}
     */
    public static EncodingRegistry getRegistry() {
        return REGISTRY;
    }

    /**
     * Returns the encoding that is used for the given model type.
     *
     * @param modelType {@link ModelType}
     * @return the encoding
     */
    public static Encoding getEncoding(ModelType modelType) {
        return getRegistry().getEncodingForModel(modelType);
    }

    /**
     * Encodes the {@code content} into a list of token ids and returns the amount of tokens.
     *
     * @param modelType {@link ModelType}
     * @param content content
     * @return the tokens
     */
    public static Long tokens(ModelType modelType, String content) {
        Encoding encoding = getEncoding(modelType);
        return (long) encoding.countTokens(content);
    }

    public static int intTokens(ModelType modelType, String content) {
        Encoding encoding = getEncoding(modelType);
        return encoding.countTokens(content);
    }

    /**
     * Encodes the {@code content} into a list of token ids and returns the amount of tokens.
     *
     * @param modelTypeName {@link ModelType} name
     * @param content content
     * @return the tokens
     */
//    public static Long tokens(String modelTypeName, String content) {
//        ModelType modelType = ModelType.fromName(modelTypeName)
//                .orElseThrow(() -> new OpenAiException("Unknown model " + modelTypeName));
//        return tokens(modelType, content);
//    }
//
//    /**
//     * Encodes the {@code messages} into a list of token ids and returns the amount of tokens.
//     *
//     * @param model model
//     * @param messages messages
//     * @return tokens
//     */
//    public static Long tokens(String model, List<ChatCompletionMessage> messages) {
//        Assert.hasText(model, "model cannot empty.");
//        Assert.notEmpty(messages, "messages cannot empty.");
//        return REPLY_PRIMED_NUM
//                + messages.stream()
//                .map(message -> {
//                    String name = message.getName();
//                    ToIntFunction<String> handler = PER_MODEL_MAP.getOrDefault(model, x -> 0);
//                    return handler.applyAsInt(name)
//                            + tokens(model, name)
//                            + tokens(model, message.getRole())
//                            + tokens(model, message.getContent());
//                })
//                .mapToLong(Long::longValue)
//                .sum();
//    }
}
