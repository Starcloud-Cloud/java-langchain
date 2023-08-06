package com.starcloud.ops.llm.langchain.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Data
@Configuration
@ConfigurationProperties(prefix = "starcloud-langchain.tools.serpapi")
public class SerpAPIToolConfig {

    private String apiKey;
}
