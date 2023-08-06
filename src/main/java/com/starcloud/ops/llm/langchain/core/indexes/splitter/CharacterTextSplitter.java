package com.starcloud.ops.llm.langchain.core.indexes.splitter;


import java.util.ArrayList;
import java.util.List;

public class CharacterTextSplitter extends BasicTextSplitter {

    @Override
    protected Long lengthFunction(String text) {
        return Long.valueOf(text.length());
    }

    @Override
    protected List<String> split(String text, int chunkSize, List<String> separators) {
        List<String> result = new ArrayList<>();
        int startIndex = 0;

        while (startIndex < text.length()) {
            System.out.println("start:" + startIndex);
            int endIndex = text.length();
            String separator = null;

            // 在当前位置查找分割符
            for (String sep : separators) {
                int index = text.indexOf(sep, startIndex);
                if (index != -1 && index < endIndex) {
                    endIndex = index;
                    separator = sep;
                }
            }

            if (endIndex - startIndex > chunkSize) {
                if (separator != null) {
                    // 使用分割符切割字符串
                    result.add(text.substring(startIndex, endIndex + 1));
                    startIndex = endIndex + separator.length();
                } else {
                    // 如果没有找到分割符，则直接截取指定长度的子串
                    result.add(text.substring(startIndex, startIndex + chunkSize + 1));
                    startIndex += chunkSize;
                }
            } else {
                result.add(text.substring(startIndex, endIndex + 1));
                startIndex = endIndex + 1;
            }
        }
        return result;
    }
}
