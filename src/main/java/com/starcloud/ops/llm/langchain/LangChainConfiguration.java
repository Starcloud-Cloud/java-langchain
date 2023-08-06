package com.starcloud.ops.llm.langchain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import javax.annotation.PostConstruct;

/**
 * 分发域启动类
 */
@Slf4j
@Configuration
@Import(cn.hutool.extra.spring.SpringUtil.class)
@ComponentScan(basePackageClasses = LangChainConfiguration.class)
@ConfigurationPropertiesScan(basePackages = "com.starcloud.ops.llm.langchain.config")
public class LangChainConfiguration {


    @PostConstruct
    public void init() {
        log.info("init StarCloud langchain ...... ");
    }


}
