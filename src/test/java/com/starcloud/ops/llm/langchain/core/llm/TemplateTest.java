package com.starcloud.ops.llm.langchain.core.llm;


import com.starcloud.ops.llm.langchain.core.SpringBootTests;
import com.starcloud.ops.llm.langchain.core.model.llm.OpenAI;
import com.starcloud.ops.llm.langchain.core.prompt.base.PromptValue;
import com.starcloud.ops.llm.langchain.core.prompt.base.template.PromptTemplate;
import com.starcloud.ops.llm.langchain.core.prompt.base.variable.BaseVariable;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.sql.DataSource;
import java.util.Arrays;

@Slf4j
public class TemplateTest extends SpringBootTests {


    @MockBean
    private DataSource dataSource;


    @Test
    public void test1() {


        PromptTemplate promptTemplate = PromptTemplate.fromTemplate("What is a good name for a company that makes {product}?");

        PromptValue promptValue = promptTemplate.formatPrompt(Arrays.asList(
                BaseVariable.newString("product", "colorful socks")
        ));


        log.info("promptValue:{}", promptValue);

    }

}
