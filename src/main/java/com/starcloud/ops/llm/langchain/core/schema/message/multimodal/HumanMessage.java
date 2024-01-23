package com.starcloud.ops.llm.langchain.core.schema.message.multimodal;

import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class HumanMessage extends MultiModalMessage {

    public HumanMessage(List<Map<String, Object>> content) {
        super(content);
    }


    public static HumanMessage ofImages(String text, List<String> images) {

        List<Map<String, Object>> messages = new ArrayList<>();

        messages.add(new HashMap<String, Object>() {{
            put(MultiModalMessage.MESSAGE_TEXT_KEY, text);
        }});

        Optional.ofNullable(images).orElse(new ArrayList<>()).forEach(img -> {
            messages.add(new HashMap<String, Object>() {{
                put(MultiModalMessage.MESSAGE_IMAGE_KEY, img);
            }});
        });

        return new HumanMessage(messages);
    }


    @Override
    public String getType() {

        return "human";
    }

}
