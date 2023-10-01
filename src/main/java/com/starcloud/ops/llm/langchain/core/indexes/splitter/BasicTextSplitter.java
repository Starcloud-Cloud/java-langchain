package com.starcloud.ops.llm.langchain.core.indexes.splitter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BasicTextSplitter {

    private static final List<String> BACK_SEPARATORS = Arrays.asList("\n", "。", "\\.", "！", "!", " ");

    private static final int DEFAULT_SIZE = 1000;

    private static final int MAX_SIZE = 3000;

    public List<String> splitText(String text, Integer chunkSize, List<String> separators) {
        if (StringUtils.isBlank(text)) {
            throw new IllegalArgumentException("text is blank");
        }
        if (chunkSize == null || chunkSize <= 100) {
            chunkSize = DEFAULT_SIZE;
        }
        chunkSize = Math.min(MAX_SIZE, chunkSize);
        if (CollectionUtils.isEmpty(separators)) {
            separators = BACK_SEPARATORS;
        }
        List<String> split = split(text, chunkSize, separators);
        return mergeSplits(split, chunkSize);
    }

    protected List<String> split(String text, int chunkSize, List<String> separators) {
        List<String> splitResult = new ArrayList<>();
        String separator = StringUtils.EMPTY;
        for (String sep : separators) {
            if (text.contains(sep)) {
                separator = sep;
                break;
            }
        }
        String[] splits = text.split(separator);
        for (String split : splits) {
            Long currentToken = lengthFunction(split);
            if (currentToken <= chunkSize) {
                if (!separator.equals(split) && !StringUtils.EMPTY.equals(split)) {
                    splitResult.add(split + separator);
                }
                continue;
            }
            splitResult.addAll(split(split, chunkSize, separators));
        }
        return splitResult;
    }

    protected List<String> mergeSplits(List<String> splits, int chunkSize) {
        List<String> mergeResult = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        long sumTokens = 0L;
        for (String split : splits) {
            long curToken = lengthFunction(split);
            if (sumTokens + curToken <= chunkSize) {
                sumTokens += curToken;
                sb.append(split);
                continue;
            }
            mergeResult.add(sb.toString());
            sb.setLength(0);
            sb.append(split);
            sumTokens = curToken;
        }
        if (sb.length() > 0) {
            mergeResult.add(sb.toString());
        }
        return mergeResult;
    }

    abstract Long lengthFunction(String text);

}
