package com.starcloud.ops.llm.langchain.core.indexes.vectorstores;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.util.List;

@org.springframework.context.annotation.Configuration
public class VectorStoreConfiguration {

    @Bean("elasticsearchClient")
    @ConditionalOnProperty(name = "starcloud-llm.vector.store", havingValue = "elasticsearch")
    public ElasticsearchClient initElasticSearchClient(@Value("${starcloud.elasticsearch.uris}") List<String> uris) {
        RestClient restClient = RestClient.builder(toHttpHost(uris)).build();
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());
        ElasticsearchClient client = new ElasticsearchClient(transport);
        return client;
    }

    @Bean("defaultRepository")
    @ConditionalOnProperty(name = "starcloud-llm.vector.store", havingValue = "default")
    public DefaultVectorStore.SegmentEmbeddingMapper initDefaultRepository(DataSource dataSource) {
        //事务
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        //创建环境
        Environment environment = new Environment("development", transactionFactory, dataSource);
        //创建配置
        Configuration configuration = new Configuration(environment);
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.addMapper(DefaultVectorStore.SegmentEmbeddingMapper.class);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        SqlSession session = sqlSessionFactory.openSession();
        DefaultVectorStore.SegmentEmbeddingMapper mapper = session.getMapper(DefaultVectorStore.SegmentEmbeddingMapper.class);
        return mapper;
    }


    public HttpHost[] toHttpHost(List<String> hosts) {
        if (CollectionUtils.isEmpty(hosts)) {
            throw new IllegalArgumentException("invalid elasticsearch configuration. spring.elasticsearch.uris is null ");
        }
        HttpHost[] httpHosts = new HttpHost[hosts.size()];
        for (int i = 0; i < hosts.size(); i++) {
            String[] strings = hosts.get(i).split(":");
            HttpHost httpHost = new HttpHost(strings[0], Integer.parseInt(strings[1]), "http");
            httpHosts[i] = httpHost;
        }
        return httpHosts;
    }
}
