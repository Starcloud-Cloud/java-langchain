# Java-langchain 介绍

🦜️ Java-langchain: 一个Java 8+的LangChain实现。在(企业)Java环境中构建强大的基于LLM的应用程序。同时包含了详细的Java入门的LLM学习课程。

- 使用说明
- Java入门LLM开发课程 [Doc](https://github.com/Starcloud-Cloud/java-langchain/tree/master/doc)

# 使用说明

## 1. 为什么要开发?

这是Java语言实现的LangChain。

大型语言模型(llm)正在作为一种变革性技术出现，使开发人员能够构建以前无法构建的应用程序。但是单独使用这些llm通常不足以创建一个真正强大的应用程序
当你可以将它们与其他计算或知识来源相结合时，真正的功能就会出现。

这个库旨在帮助开发这类应用程序和帮助Java开发迅速学习到LLM开发的必要知识。

下面的例子可以在课程中查看 [doc-example](https://github.com/Starcloud-Cloud/java-langchain/tree/master/doc)


## 2. 使用示例
 - 待整理

## 3. 集成
### 3.1 LLMs
- [OpenAI](src/test/java/com/starcloud/ops/llm/langchain/core/llm/OpenAITest.java), (support stream)

- [千义通问](src/test/java/com/starcloud/ops/llm/langchain/core/llm/ChatQwenAITest.java)


### 3.2 向量存储
- [Mysql]()
- [Elasticsearch]()

### 3.3 智能体
- 智能体 只使用GPT4去实现了，是现在效果的最好的方案。

## 4. 快速入门指南

本教程向您快速介绍如何使用LangChain构建端到端语言模型应用程序。

### 4.1 Maven

1. 将JitPack仓库添加到构建文件中
```xml
<repositories>
    <repository>
         <id>jitpack.io</id>
         <url>https://jitpack.io</url>
    </repository>
</repositories>
```

2. 增加依赖
```xml
<dependency>
    <groupId>com.github.Starcloud-Cloud</groupId>
    <artifactId>java-langchain</artifactId>
    <version>v1.0.0</version>
</dependency>
```
查看最新版本 [version](https://jitpack.io/#Starcloud-Cloud/java-langchain)

[![](https://jitpack.io/v/Starcloud-Cloud/java-langchain.svg)](https://jitpack.io/#Starcloud-Cloud/java-langchain)


### 4.2 LLMs
从语言模型获得预测结果。LangChain的基本构建块是LLM，它接收文本并生成更多的文本。

```java
OpenAI llm = new OpenAI();
log.info("result : {}", llm.call("Hi there! what you name?"));
```
现在我们可以传入文本并得到预测!
```shell
result : 
My name is Priya.
```
### 4.3 聊天

聊天模型是语言模型的一种变体。虽然聊天模型在底层使用语言模型，但它们公开的接口有点不同:它们公开的不是“文本输入，文本输出”的API，而是一个以“聊天消息”为输入和输出的接口。

```java
ChatOpenAI chatOpenAI = new ChatOpenAI();
log.info("result: {}", JSONUtil.toJsonStr(chatOpenAI.predictMessages(Arrays.asList(new HumanMessage("hi, what you name?")))));
```

```shell
 result: {"content":"Hello! I am an AI language model developed by OpenAI, and I don't have a personal name. However, you can call me GPT-3 or ChatGPT if you'd like! How can I assist you today?","additionalArgs":{}}

```

了解聊天模型与普通LLM的不同之处是很有用的，但如果能够将它们同等对待，通常也会很方便。LangChain还公开了一个接口，可以通过它与聊天模型进行交互，就像普通的LLM一样。你可以通过`predict`接口访问它。

```java
ChatOpenAI chatOpenAI = new ChatOpenAI();
log.info(chatOpenAI.predict("hi, what you name?"));
```
```shell
Hello! I am an AI language model developed by OpenAI, so I don't have a personal name. You can call me OpenAI Assistant. How can I assist you today?
```

### 4.4 提示词模版
大多数LLM应用程序不会将用户输入直接传递给LLM。通常，他们会将用户输入添加到一个更大的文本中，称为提示模板，它为手头的特定任务提供额外的上下文。

在前面的例子中，我们传递给模型的文本包含了生成公司名称的指令。对于我们的应用程序，如果用户只需要提供公司/产品的描述，而不必担心给出模型指令，那就太好了。


```java
PromptTemplate promptTemplate = PromptTemplate.fromTemplate("What is a good name for a company that makes {product}?");
PromptValue promptValue = promptTemplate.formatPrompt(Arrays.asList(
    BaseVariable.newString("product", "colorful socks")
));
log.info("promptValue:{}", promptValue);

```

```shell
promptValue:StringPromptValue(str=What is a good name for a company that makes colorful socks?)
```

### 4.5 语言链

现在我们有了一个model和一个prompt模板，我们想要将两者结合起来。链为我们提供了一种链接(或链接)多个基元的方法，如模型、提示符和其他链。

更多示例内容: [Chains](src/test/java/com/starcloud/ops/llm/langchain/learning/langchain/code/Chains.java)


### 4.6 智能体

我们的第一个链运行预先确定的步骤序列。为了处理复杂的工作流，我们需要能够根据输入动态地选择操作。

智能体就是这样做的:它们使用语言模型来确定采取哪些行动以及以什么顺序进行。智能体可以访问工具，它们重复选择工具，运行工具，并观察输出，直到它们提出最终答案。


```java
ChatOpenAI chatOpenAI = new ChatOpenAI();

//这里我们将参数temperature设置为0.0，从而减少生成答案的随机性。
chatOpenAI.setTemperature(0.0);

List<BaseTool> tools = LoadTools.loadTools(Arrays.asList(CalculatorTool.class), chatOpenAI);

OpenAIFunctionsAgent baseSingleActionAgent = OpenAIFunctionsAgent.fromLLMAndTools(chatOpenAI, tools);

AgentExecutor agentExecutor = AgentExecutor.fromAgentAndTools(tools, chatOpenAI, baseSingleActionAgent, baseSingleActionAgent.getCallbackManager());

agentExecutor.run("计算300的25%");

```

更多示例内容: [Agent](src/test/java/com/starcloud/ops/llm/langchain/learning/langchain/code/Agent.java)



### 4.7 记忆

到目前为止，我们看到的链和代理都是无状态的，但对于许多应用程序来说，必须引用过去的交互。显然，这就是聊天机器人的情况，你希望它在过去消息的上下文中理解新消息。

内存模块提供了一种维护应用程序状态的方法。基本内存接口很简单:它允许您根据最新的运行输入和输出更新状态，并允许您使用存储状态修改(或上下文化)下一个输入。

有许多内置的存储系统。其中最简单的是一个缓冲存储器，它只是将最后几个输入/输出添加到当前输入

```java
ConversationBufferWindowMemory memory = new ConversationBufferWindowMemory(1);

memory.saveContext(BaseVariable.newString("input", "你好，我叫皮皮鲁"), BaseVariable.newString("output", "你好啊，我叫鲁西西"));

log.info("loadMemoryVariables: {}", memory.loadMemoryVariables());

memory.saveContext(BaseVariable.newString("input", "很高兴和你成为朋友！"), BaseVariable.newString("output", "是的，让我们一起去冒险吧！"));

log.info("loadMemoryVariables: {}", memory.loadMemoryVariables());

```

更多示例内容: [Memory](src/test/java/com/starcloud/ops/llm/langchain/learning/langchain/code/Memory.java)



## 5. 支持

不要犹豫，尽管问!

如果你在java-langchain中发现一个bug，[打开一个issue](https://github.com/Starcloud-Cloud/java-langchain/issues)

## 6. 贡献
这是一个活跃的开源项目。我们始终向想要使用该系统或为其做出贡献的人开放。请注意，pull requests应该合并到**dev**分支中。

如果您正在寻找适合您技能的实施任务，请联系我。

---

# Java入门LLM开发课程

课程总共包含4大部分内容, 内容循序渐进，适合Java同学第一次接触大语言模型的应用开发过程。
对应的代码也放在了`test/learning` 内容

1. 面向开发者的提示工程 (promptdevelopment)
2. 搭建基于 ChatGPT 的问答系统 (chagptapi)
3. 使用 LangChain 开发应用程序 (langchain)
4. 使用 LangChain 访问个人数据(开发中)

学习入口: [Doc](https://github.com/Starcloud-Cloud/java-langchain/tree/master/doc)

