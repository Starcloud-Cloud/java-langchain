package com.starcloud.ops.llm.langchain.core;

import com.starcloud.ops.llm.langchain.LangChainConfiguration;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = {LangChainConfiguration.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class SpringBootTests {
}
