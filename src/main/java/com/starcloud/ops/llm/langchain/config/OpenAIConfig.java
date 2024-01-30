package com.starcloud.ops.llm.langchain.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;


@Data
@Configuration
@ConfigurationProperties(prefix = "starcloud-langchain.model.llm.openai")
public class OpenAIConfig {

    private String apiKey;

    private Long timeOut;

    private List<String> proxyHosts;

    private int proxyPort;

    private List<String> proxyHttps;

    private int proxyHttpsPort;

    private Boolean azure;

    private String azureKey;
}
