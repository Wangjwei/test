package com.example.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.admin.entity.User;
import com.example.admin.service.UserESService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.Suggest;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class UserESServiceImpl implements UserESService {

    private final RestHighLevelClient restHighLevelClient;

    public UserESServiceImpl(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    /**
     *  创建索引
     * @param idxName 索引名称
     * @param idxSQL 索引描述
     */
    @Override
    public void createIndex(String idxName,String idxSQL){
        try {
            if (!this.indexExist(idxName)) {
                log.info(" idxName={} 已经存在,idxSql={}",idxName,idxSQL);
                return;
            }
            CreateIndexRequest request = new CreateIndexRequest(idxName);
            buildSetting(request);
            request.mapping(idxSQL, XContentType.JSON);
//         request.settings() 手工指定Setting
            CreateIndexResponse res = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
            if (!res.isAcknowledged()) {
                throw new RuntimeException("初始化失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * 判断某个index是否存在
     * @param idxName index名
     * @return
     * @throws Exception
     */
    private boolean indexExist(String idxName) throws Exception {
        GetIndexRequest request = new GetIndexRequest(idxName);
        request.local(false);
        request.humanReadable(true);
        request.includeDefaults(false);
        request.indicesOptions(IndicesOptions.lenientExpandOpen());
        return restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
    }

    /**
     * 设置分片
     * @param request
     */
    private void buildSetting(CreateIndexRequest request){
        request.settings(Settings.builder().put("index.number_of_shards",6)
                .put("index.number_of_replicas",1));
    }

    /**
     *
     * @param idxName 索引名
     * @param entity 文档对象
     */
    @Override
    public void insertOrUpdateOne(String idxName, User entity) {
        IndexRequest request = new IndexRequest(idxName);
        //log.info("Data : id={},entity={}",entity.getId(),JSON.toJSONString(entity.getData()));
        request.id(entity.getId().toString());
        request.source(JSON.toJSONString(entity), XContentType.JSON);
        try {
            restHighLevelClient.index(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 批量插入文档数据
     * @param idxName
     * @param list
     */
    @Override
    public void insertBatch(String idxName, List<User> list) {
        BulkRequest request = new BulkRequest();
        list.forEach(item -> request.add(new IndexRequest(idxName).id(item.getId().toString())
                .source(JSON.toJSONString(item), XContentType.JSON)));
        try {
            restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 批量删除
     * @param idxName 索引名称
     * @param idList 待删除列表
     * @param <T>
     */
    @Override
    public <T> void deleteBatch(String idxName, Collection<T> idList) {
        BulkRequest request = new BulkRequest();
        idList.forEach(item -> request.add(new DeleteRequest(idxName, item.toString())));
        try {
            restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 搜索
     * @param idxName
     * @param builder
     * @return
     */
    @Override
    public List<String> searchCompletionSuggest(String idxName, SearchSourceBuilder builder) {
        SearchRequest request = new SearchRequest(idxName);
        request.source(builder);
        try {
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(response.getSuggest().iterator(), Spliterator.ORDERED), false)
                    .flatMap(suggestion -> suggestion.getEntries().get(0).getOptions().stream())
                    .map((Suggest.Suggestion.Entry.Option option) -> option.getText().toString())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除index
     * @param idxName
     */
    @Override
    public void deleteIndex(String idxName) {
        try {
            if (!this.indexExist(idxName)) {
                log.info(" idxName={} 已经存在",idxName);
                return;
            }
            restHighLevelClient.indices().delete(new DeleteIndexRequest(idxName), RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * deleteByQuery
     * @param idxName
     * @param builder
     */
    @Override
    public void deleteByQuery(String idxName, QueryBuilder builder) {
        DeleteByQueryRequest request = new DeleteByQueryRequest(idxName);
        request.setQuery(builder);
        //设置批量操作数量,最大为10000
        request.setBatchSize(10000);
        request.setConflicts("proceed");
        try {
            restHighLevelClient.deleteByQuery(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
