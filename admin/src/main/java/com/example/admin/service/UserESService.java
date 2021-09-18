package com.example.admin.service;

import com.example.admin.entity.User;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.Collection;
import java.util.List;

public interface UserESService {
    void createIndex(String idxName,String idxSQL);

    void insertOrUpdateOne(String idxName, User entity);

    void insertBatch(String idxName, List<User> list);

    <T> void deleteBatch(String idxName, Collection<T> idList);

    List<String> searchCompletionSuggest(String idxName, SearchSourceBuilder builder);

    void deleteIndex(String idxName);

    void deleteByQuery(String idxName, QueryBuilder builder);
}
