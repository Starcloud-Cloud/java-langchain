package com.starcloud.ops.llm.langchain.core.tools;


import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.starcloud.ops.llm.langchain.config.SerpAPIToolConfig;
import com.starcloud.ops.llm.langchain.core.tools.base.BaseTool;
import kong.unirest.HttpResponse;
import kong.unirest.JacksonObjectMapper;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;


@Slf4j
@Data
public class SerpAPITool extends BaseTool<SerpAPITool.Request, String> {

    private String name = "Search";

    private String description = "A search engine. Useful for when you need to answer questions about current events. Input should be a search query.";

    @JsonIgnore
    private String lr = "lang_zh-CN|lang_en";

    @JsonIgnore
    private String apiKey;

    public SerpAPITool(String serpapiApiKey) {
        Unirest.config().setObjectMapper(new JacksonObjectMapper());
        this.apiKey = serpapiApiKey;
    }

    public SerpAPITool() {

        SerpAPIToolConfig serpAPIToolConfig = SpringUtil.getBean(SerpAPIToolConfig.class);
        Unirest.config().setObjectMapper(new JacksonObjectMapper());
        this.apiKey = serpAPIToolConfig.getApiKey();
    }

    @Override
    protected String _run(SerpAPITool.Request input) {

        String result = "";

        try {

            HttpResponse<JsonNode> response = Unirest.post("https://google.serper.dev/search")
                    .header("X-API-KEY", this.getApiKey())
                    .header("Content-Type", "application/json")
                    .body(input)
                    .asJson();

            return this.processResponse(response);

        } catch (Exception e) {

            log.error("SerpAPITool is fail {}", e.getMessage(), e);
        }

        return result;
    }


    private String processResponse(HttpResponse<JsonNode> json) {

        String toret = "";

        try {

            if (json.getBody() != null) {

                JSONObject body = json.getBody().getObject();

                if (StrUtil.isBlank(toret)) {
                    toret = Optional.ofNullable(body).map((d) -> d.optJSONObject("answerBox")).map((d) -> d.optString("answer")).orElse("");
                }


                if (StrUtil.isBlank(toret)) {
                    toret = Optional.ofNullable(body).map((d) -> d.optJSONObject("answerBox")).map((d) -> d.optString("snippet")).orElse("");
                }


                if (StrUtil.isBlank(toret)) {
                    toret = Optional.ofNullable(body).map((d) -> d.optJSONObject("answerBox")).map((d) -> d.optString("snippetHighlightedWords")).orElse("");
                }


                if (StrUtil.isBlank(toret)) {
                    toret = Optional.ofNullable(body).map((d) -> d.optJSONObject("sportsResults")).map((d) -> d.optString("gameSpotlight")).orElse("");
                }


                if (StrUtil.isBlank(toret)) {
                    toret = Optional.ofNullable(body).map((d) -> d.optJSONObject("knowledgeGraph")).map((d) -> d.optString("description")).orElse("");
                }


                if (StrUtil.isBlank(toret)) {

                    JSONObject jsonObject = Optional.ofNullable(body).map((d) -> (JSONObject) d.optJSONArray("organic").get(0)).orElse(null);

                    toret = Optional.ofNullable(jsonObject).map((d) -> d.optString("snippet")).orElseGet(() -> Optional.ofNullable(jsonObject).map((d) -> d.optString("link")).orElse(""));

                }
            }

        } catch (Exception e) {

            log.error("SerpAPITool is fail: {}", e.getMessage(), e);
        }

        if (StrUtil.isBlank(toret)) {
            toret = "No good search result found";
        }

        return toret;
    }

    @Data
    public static class Request {

        @JsonProperty(required = true)
        @JsonPropertyDescription("Parameter defines the query you want to search.")
        private String q;

    }

}

