package com.example.test.config;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Api("elasticsearch配置类")
@Configuration
public class ESConfig {
    /**
     * ES 端口9200与9300的区别：
     *
     * 9200作为Http协议，主要用于外部通讯
     * 9300作为Tcp协议，jar之间就是通过tcp协议通讯
     * ES集群之间是通过9300进行通讯
     * */
    @Bean
    @ApiOperation("注入client")
    public RestHighLevelClient restHighLevelClient() {
        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("127.0.0.1", 9200, "http")));
    }
}
