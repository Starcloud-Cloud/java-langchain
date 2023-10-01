package com.starcloud.ops.llm.langchain.core.indexes.splitter;


import cn.hutool.core.collection.CollectionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CharacterTextSplitter extends BasicTextSplitter {

    @Override
    protected Long lengthFunction(String text) {
        return Long.valueOf(text.length());
    }

//    @Override
//    protected List<String> split(String text, int chunkSize, List<String> separators) {
//        List<String> result = new ArrayList<>();
//        StringJoiner sj = new StringJoiner("|");
//        for (String sep : separators) {
//            sj.add(sep);
//        }
//
//        String[] arr = text.split(sj.toString()); // 使用正则表达式拆分字符串
//        for (String s : arr) {
//            if (s.length() <= chunkSize * 2) {
//                result.add(s);
//            } else {
//                int start = 0;
//                while (start < s.length()) {
//                    result.add(s.substring(start, Math.min(start + chunkSize, s.length())));
//                    start += chunkSize;
//                }
//            }
//        }
//        return result;
//    }

    @Override
    protected List<String> split(String text, int chunkSize, List<String> separators) {
        List<String> list = null;
        for (String separator : separators) {
            if (".".equals(separator)) {
                separator = "\\.";
            }
            if (list == null) {
                String[] splits = text.split(separator);
                list = CollectionUtil.toList(splits);
                continue;
            }
            Pattern pattern = Pattern.compile(separator);
            for (int i = 0; i < list.size(); i++) {
                String split = list.get(i);
                if (split.length() > chunkSize && pattern.matcher(split).find()) {
                    String[] splits = split.split(separator);
                    list.remove(i);
                    list.addAll(i, CollectionUtil.toList(splits));
                }
            }
        }

        List<String> result = new ArrayList<>();
        for (String s : list) {
            if (s.length() <= (chunkSize * 3 / 2)) {
                result.add(s);
            } else {
                int start = 0;
                while (start < s.length()) {
                    result.add(s.substring(start, Math.min(start + chunkSize, s.length())));
                    start += chunkSize;
                }
            }
        }
        return result;
    }


}
