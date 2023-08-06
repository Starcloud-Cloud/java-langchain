package com.starcloud.ops.llm.langchain.core.tools;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.llm.langchain.core.tools.base.BaseTool;
import kotlin.jvm.Transient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Data
@Slf4j
public class SerpapiXXTool extends BaseTool<SerpapiXXTool.Request, String> {


    private String name = "Search";

    private String description = "A search engine. Useful for when you need to answer questions about current events. Input should be a search query.";

    @Transient
    private String lr = "lang_zh-CN|lang_en";

    @Transient
    private String apiKey;

    public SerpapiXXTool(String apiKey) {

        this.apiKey = apiKey;
    }

    @Override
    protected String _run(SerpapiXXTool.Request input) {

        SerpapiXXTool.Request request = JSONUtil.toBean(input.toString(), SerpapiXXTool.Request.class);

        Map<String, String> parameter = new HashMap<>();
        parameter.put("q", request.getQ());
        parameter.put("lr", this.lr);
        parameter.put("start", "0");
        parameter.put("num", "10");

        String result = "";

        try {
            // GoogleSearch googleSearch = new GoogleSearch(parameter, this.apiKey);
            // com.google.gson.JsonObject jsonObject = googleSearch.getJson();
            //
            // String answerBox = Optional.ofNullable(jsonObject).map(b -> b.getAsJsonObject("answer_box")).map(b -> b.get("snippet").getAsString()).orElse("");
            //
            // result = answerBox;
        } catch (Exception e) {

            log.error("googleSearch is fail {}", e.getMessage(), e);
        }

        return result;
    }

    @Data
    public static class Request {

        @JsonProperty(required = true)
        @JsonPropertyDescription("Parameter defines the query you want to search.")
        private String q;

    }

}
