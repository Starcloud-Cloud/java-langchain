package com.starcloud.ops.llm.langchain.core.tools;


import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.starcloud.ops.llm.langchain.config.SerpAPIToolConfig;
import com.starcloud.ops.llm.langchain.core.tools.base.BaseTool;
import com.starcloud.ops.llm.langchain.core.tools.base.ToolResponse;
import kong.unirest.HttpResponse;
import kong.unirest.JacksonObjectMapper;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Data
public class SerpAPITool extends BaseTool<SerpAPITool.Request> {

    public static String GL = "cn";

    public static String HL = "lang_zh-CN|lang_en";

    private String name = "GoogleSearch";

    private String description = "A search engine. Useful for when you need to answer questions about current events. Input should be a search query.";


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
    protected ToolResponse _run(Request input) {

        ToolResponse result = ToolResponse.buildObservation(null);

        try {

            HttpResponse<JsonNode> response = Unirest.post("https://google.serper.dev/search")
                    .header("X-API-KEY", this.getApiKey())
                    .header("Content-Type", "application/json")
                    .body(input)
                    .asJson();

            if (response.getBody() != null) {
                JSONObject body = response.getBody().getObject();
                result = ToolResponse.buildObservation(this.processResponseStr(this.processResponse(body)));
            }

        } catch (Exception e) {

            log.error("SerpAPITool is fail {}", e.getMessage(), e);
        }

        return result;
    }

    public List<SearchInfoDetail> runGetInfo(Request input) {

        List<SearchInfoDetail> result = new ArrayList<>();

        HttpResponse<JsonNode> response = Unirest.post("https://google.serper.dev/search")
                .header("X-API-KEY", this.getApiKey())
                .header("Content-Type", "application/json")
                .body(input)
                .asJson();

        if (response.getBody() != null) {

            JSONObject body = response.getBody().getObject();
            return this.processResponse(body);
        }

        return result;
    }

    public List<SearchInfoDetail> runGetImages(Request input) {

        List<SearchInfoDetail> result = new ArrayList<>();

        HttpResponse<JsonNode> response = Unirest.post("https://google.serper.dev/images")
                .header("X-API-KEY", this.getApiKey())
                .header("Content-Type", "application/json")
                .body(input)
                .asJson();

        if (response.getBody() != null) {

            JSONObject body = response.getBody().getObject();
            return this.processImagesResponse(body);
        }

        return result;
    }


    public List<SearchInfoDetail> runGetNews(Request input) {

        List<SearchInfoDetail> result = new ArrayList<>();

        HttpResponse<JsonNode> response = Unirest.post("https://google.serper.dev/news")
                .header("X-API-KEY", this.getApiKey())
                .header("Content-Type", "application/json")
                .body(input)
                .asJson();

        if (response.getBody() != null) {

            JSONObject body = response.getBody().getObject();
            return this.processNewsResponse(body);
        }

        return result;
    }


    public String processResponseStr(List<SearchInfoDetail> searchInfoDetails) {

        String toret = Optional.ofNullable(searchInfoDetails).orElse(new ArrayList<>()).stream().map(SearchInfoDetail::getContent).findFirst().orElse("");

        if (StrUtil.isBlank(toret)) {
            toret = "No good search result found";
        }

        return toret;
    }


    public List<SearchInfoDetail> processResponse(JSONObject body) {

        List<SearchInfoDetail> result = new ArrayList<>();

        try {

            if (body != null) {

                SearchInfoDetail answerBox = Optional.ofNullable(body).map((d) -> d.optJSONObject("answerBox")).map((d) -> {

                    if (StrUtil.isNotBlank(d.optString("answer"))) {
                        return SearchInfoDetail.builder().type("answerBox").title(d.optString("title")).content(d.optString("answer")).build();

                    } else if (StrUtil.isNotBlank(d.optString("snippet"))) {
                        return SearchInfoDetail.builder().type("answerBox").title(d.optString("title")).content(d.optString("snippet")).build();

                    } else if (StrUtil.isNotBlank(d.optString("snippetHighlightedWords"))) {
                        return SearchInfoDetail.builder().type("answerBox").title(d.optString("title")).content(d.optString("snippetHighlightedWords")).build();
                    }

                    return null;

                }).orElse(null);

                result.add(answerBox);

                SearchInfoDetail sportsResults = Optional.ofNullable(body).map((d) -> d.optJSONObject("sportsResults")).map((d) -> {

                    if (StrUtil.isNotBlank(d.optString("gameSpotlight"))) {
                        return SearchInfoDetail.builder().type("sportsResults").title(d.optString("title")).content(d.optString("gameSpotlight")).link(d.optString("link")).build();

                    }
                    return null;

                }).orElse(null);
                result.add(sportsResults);

                SearchInfoDetail knowledgeGraph = Optional.ofNullable(body).map((d) -> d.optJSONObject("knowledgeGraph")).map((d) -> {

                    if (StrUtil.isNotBlank(d.optString("description"))) {
                        return SearchInfoDetail.builder().type("knowledgeGraph").title(d.optString("title")).content(d.optString("description")).link(d.optString("link")).build();
                    }

                    return null;

                }).orElse(null);
                result.add(knowledgeGraph);


                List<JSONObject> jsonObjects = Optional.ofNullable(body).map((d) -> {
                    return d.optJSONArray("organic");
                }).map(JSONArray::toList).orElse(new ArrayList<>());

                result.addAll(Optional.ofNullable(jsonObjects).orElse(new ArrayList<>()).stream().map((object) -> {
                    if (StrUtil.isNotBlank(object.optString("link"))) {
                        return SearchInfoDetail.builder().type("organic").title(object.optString("title")).content(object.optString("snippet")).link(object.optString("link")).time(object.optString("date")).build();
                    }
                    return null;
                }).filter(Objects::nonNull).collect(Collectors.toList()));


                List<JSONObject> peopleAlsoAsk = Optional.ofNullable(body).map((d) -> {
                    return d.optJSONArray("peopleAlsoAsk");
                }).map(JSONArray::toList).orElse(new ArrayList<>());

                result.addAll(Optional.ofNullable(peopleAlsoAsk).orElse(new ArrayList<>()).stream().map((object) -> {
                    if (StrUtil.isNotBlank(object.optString("question"))) {
                        return SearchInfoDetail.builder().type("question").title(object.optString("title")).content(object.optString("question")).link(object.optString("link")).build();
                    }
                    return null;
                }).filter(Objects::nonNull).collect(Collectors.toList()));


                List<JSONObject> relatedSearches = Optional.ofNullable(body).map((d) -> {
                    return d.optJSONArray("relatedSearches");
                }).map(JSONArray::toList).orElse(new ArrayList<>());

                result.addAll(Optional.ofNullable(relatedSearches).orElse(new ArrayList<>()).stream().map((object) -> {
                    if (StrUtil.isNotBlank(object.optString("query"))) {
                        return SearchInfoDetail.builder().type("relatedSearches").content(object.optString("query")).build();
                    }
                    return null;
                }).filter(Objects::nonNull).collect(Collectors.toList()));


            }

        } catch (Exception e) {

            log.error("SerpAPITool is fail: {}", e.getMessage(), e);
        }

        return Optional.ofNullable(result).orElse(new ArrayList<>()).stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<SearchInfoDetail> processImagesResponse(JSONObject body) {

        List<SearchInfoDetail> result = new ArrayList<>();

        try {

            if (body != null) {


                List<JSONObject> imagesSearches = Optional.ofNullable(body).map((d) -> {
                    return d.optJSONArray("images");
                }).map(JSONArray::toList).orElse(new ArrayList<>());

                result.addAll(Optional.ofNullable(imagesSearches).orElse(new ArrayList<>()).stream().map((object) -> {
                    if (StrUtil.isNotBlank(object.optString("imageUrl"))) {
                        return SearchInfoDetail.builder().type("image").title(object.optString("title")).imageUrl(object.optString("imageUrl")).link(object.optString("link")).build();
                    }
                    return null;
                }).filter(Objects::nonNull).collect(Collectors.toList()));


            }

        } catch (Exception e) {

            log.error("SerpAPITool is fail: {}", e.getMessage(), e);
        }

        return Optional.ofNullable(result).orElse(new ArrayList<>()).stream().filter(Objects::nonNull).collect(Collectors.toList());
    }


    public List<SearchInfoDetail> processNewsResponse(JSONObject body) {

        List<SearchInfoDetail> result = new ArrayList<>();

        try {

            if (body != null) {


                List<JSONObject> imagesSearches = Optional.ofNullable(body).map((d) -> {
                    return d.optJSONArray("news");
                }).map(JSONArray::toList).orElse(new ArrayList<>());

                result.addAll(Optional.ofNullable(imagesSearches).orElse(new ArrayList<>()).stream().map((object) -> {
                    if (StrUtil.isNotBlank(object.optString("imageUrl"))) {
                        return SearchInfoDetail.builder().type("news").title(object.optString("title")).content(object.optString("snippet")).link(object.optString("link")).build();
                    }
                    return null;
                }).filter(Objects::nonNull).collect(Collectors.toList()));


            }

        } catch (Exception e) {

            log.error("SerpAPITool is fail: {}", e.getMessage(), e);
        }

        return Optional.ofNullable(result).orElse(new ArrayList<>()).stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Data
    public static class Request {

        @JsonProperty(required = true)
        @JsonPropertyDescription("Parameter defines the query you want to search.")
        private String q;

        private String gl;

        private String hl;

    }


    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class SearchInfoDetail implements Serializable {

        private String type;

        private String link;

        private String imageUrl;

        private String time;

        private String title;

        private String content;

    }

}

