package com.starcloud.ops.llm.langchain.config;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;


@Data
@Configuration
@ConfigurationProperties(prefix = "starcloud-langchain.model.llm.qwen")
public class QwenAIConfig {

    private String apiKey;

    /**
     * 支持多key,自动随机使用
     */
    private List<String> apiKeys;

    public String randomApiKey() {

        if (CollectionUtil.isNotEmpty(this.getApiKeys())) {
            return this.getApiKeys().get(RandomUtil.randomInt(this.getApiKeys().size()));
        }

        return this.getApiKey();
    }


}
