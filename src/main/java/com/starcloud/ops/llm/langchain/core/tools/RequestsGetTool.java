package com.starcloud.ops.llm.langchain.core.tools;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.JsonNode;
import com.starcloud.ops.llm.langchain.core.tools.base.BaseTool;
import com.starcloud.ops.llm.langchain.core.tools.base.ToolResponse;
import lombok.Data;


/**
 * http get 请求工具
 */
@Data
public class RequestsGetTool extends BaseTool<RequestsGetTool.Request> {

    private int timeOut = 1000 * 10;

    private String name = "requests_get";

    private String description = "A portal to the internet. Use this when you need to get specific content from a website. Input should be a  url (i.e. https://www.google.com). The output will be the text response of the GET request.";

    @Override
    protected ToolResponse _run(Request input) {

        return ToolResponse.buildObservation(HttpUtil.get(input.getUrl(), this.timeOut));
    }


    @Data
    public static class Request {

        @JsonProperty(required = true)
        @JsonPropertyDescription("a website url")
        private String url;

        public Request(String url) {
            this.url = url;
        }
    }
}
