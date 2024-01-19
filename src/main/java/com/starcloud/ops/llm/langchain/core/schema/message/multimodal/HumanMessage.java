package com.starcloud.ops.llm.langchain.core.schema.message.multimodal;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class HumanMessage extends MultiModalMessage {

    public HumanMessage(List<Map<String, Object>> content) {
        super(content);
    }


    public static HumanMessage ofTestImages(String... args) {

        List<Map<String, Object>> messages = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            int finalI = i;
            //第一个默认放text
            if (finalI == 0) {
                messages.add(new HashMap<String, Object>() {{
                    put(MultiModalMessage.MESSAGE_TEXT_KEY, args[finalI]);
                }});
            } else {
                messages.add(new HashMap<String, Object>() {{
                    put(MultiModalMessage.MESSAGE_IMAGE_KEY, args[finalI]);
                }});
            }
        }

        return new HumanMessage(messages);
    }


    @Override
    public String getType() {

        return "human";
    }

}
