package com.starcloud.ops.llm.langchain.core.schema.message.multimodal;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.starcloud.ops.llm.langchain.core.schema.message.*;
import com.starcloud.ops.llm.langchain.core.utils.JsonUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


@NoArgsConstructor
@Data
public abstract class MultiModalMessage extends BaseMessage {

    public static String MESSAGE_IMAGE_KEY = "image";

    public static String MESSAGE_TEXT_KEY = "text";

    private List<Map<String, Object>> contents;


    public MultiModalMessage(List<Map<String, Object>> content) {
        if (CollectionUtil.isNotEmpty(content)) {
            this.contents = content;
        } else {
            this.contents = new ArrayList<>();
        }
    }

    public MultiModalMessage(String content) {
        if (!StrUtil.isEmpty(content)) {

            this.contents = new ArrayList<Map<String, Object>>() {{
                add(new HashMap<String, Object>() {{
                    put(MESSAGE_TEXT_KEY, content);
                }});
            }};
        } else {
            this.contents = new ArrayList<>();
        }
    }

    /**
     * 只返回字符串，计算token用
     *
     * @return
     */
    @Override
    public String getContent() {

        List<String> items = new ArrayList<>();
        items.addAll(this.getText());
        items.addAll(this.getImages());
        return Optional.of(items).orElse(new ArrayList<>()).stream().collect(Collectors.joining("\r\n"));
    }


    /**
     * 获取一次对话中所有图片
     *
     * @return
     */
    public List<String> getImages() {

        return Optional.ofNullable(contents).orElse(new ArrayList<>()).stream().map(stringObjectMap -> {
            return (String) stringObjectMap.getOrDefault(MESSAGE_IMAGE_KEY, null);
        }).filter(Objects::nonNull).collect(Collectors.toList());

    }

    /**
     * 获取一次对话中所有图片
     *
     * @return
     */
    public List<String> getText() {

        return Optional.ofNullable(contents).orElse(new ArrayList<>()).stream().map(stringObjectMap -> {
            return (String) stringObjectMap.getOrDefault(MESSAGE_TEXT_KEY, null);
        }).filter(Objects::nonNull).collect(Collectors.toList());

    }

}
