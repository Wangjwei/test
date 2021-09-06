package com.example.test.utils;

import io.swagger.annotations.Api;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

@Api("创建于elasticsearch连接")
public class ESClient {
    public static RestHighLevelClient getClient() {
        //创建HttpHost
        HttpHost host = new HttpHost("127.0.0.1", 9200);
        // 创建RestClientBuilder
        RestClientBuilder builder = RestClient.builder(host);
        // 创建RestHighLevelClient
        RestHighLevelClient client = new RestHighLevelClient(builder);
        return client;
    }
}