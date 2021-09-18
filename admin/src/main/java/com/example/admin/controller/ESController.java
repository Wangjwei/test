package com.example.admin.controller;

import com.example.admin.config.Result;
import com.example.admin.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.TermVectorsRequest;
import org.elasticsearch.client.core.TermVectorsResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
public class ESController {

    @Resource
    private RestHighLevelClient client;

    @Resource
    private UserService userService;

    @ApiOperation("插入数据到索引中")
    @PostMapping("/addIndex")
    public Result insertUser() throws Exception {
        userService.insertUser();
        return Result.ok("插入成功");
    }

    @ApiOperation("根据用户输入内容获取自动补全提示语")
    @GetMapping("/getSearch")
    public Result searchCompletionUser(String searchValue) {
        List<String> stringList = userService.searchCompletionSuggest(searchValue);
        stringList.forEach(log::info);
        return Result.ok(stringList);
    }

    @ApiOperation("高亮搜索")
    @GetMapping("/highLightSearch")
    public Result search() throws IOException {
        //搜索条件 不带参数，表示查询所有索引
        SearchRequest searchRequest = new SearchRequest("user");
        //添加大部分查询参数到 SearchSourceBuilder，接收QueryBuilders构建的查询参数
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery("daj","address", "name", "address.pinyin", "name.pinyin")) //同时对多个字段进行模糊匹配
                // 设置当前查询的超时时间
                .timeout(new TimeValue(60, TimeUnit.SECONDS))
                // 设置查询结果的页大小，默认是10
                .size(5)
                //搜索结果突出
                .highlighter(new HighlightBuilder()
                        .field("address.pinyin")
                        .requireFieldMatch(false) // 只需一个高亮
                        .preTags("<b style='color:red'>")
                        .postTags("</b>"));
        //执行搜索 添加 SearchSourceBuilder 到 SearchRequest
        searchRequest.source(searchSourceBuilder);
        SearchResponse search = client.search(searchRequest, RequestOptions.DEFAULT);
        //解析结果
        List<Object> queryList = new LinkedList<>();
        for (SearchHit documentFields : search.getHits().getHits()) {
            Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();//原来的结果
            HighlightField field = documentFields.getHighlightFields().get("address.pinyin");
            //解析高亮的字段，将原来的字段替换成高亮字段
            Text[] texts = field.fragments();
            StringBuilder n_text = new StringBuilder();
            for (Text text : texts) {
                n_text.append(text);
            }
            sourceAsMap.put("address", n_text.toString());//高亮字段替换原来的内容
            queryList.add(sourceAsMap);

        }
        return Result.ok(queryList);
    }

    @ApiOperation("词频统计aggregate")
    @GetMapping("aggregate")
    public void aggregate() throws IOException {
        SearchRequest searchRequest = new SearchRequest("user");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        TermsAggregationBuilder aggregation = AggregationBuilders.terms("person-aggregate")
                .field("firstname");
        searchSourceBuilder.aggregation(aggregation);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        Aggregations aggregations = searchResponse.getAggregations();
        Terms byCompanyAggregation = aggregations.get("person-aggregate");
        List<? extends Terms.Bucket> elasticBucket = byCompanyAggregation.getBuckets();
        elasticBucket.forEach(el -> {
            log.info("key:" + el.getKeyAsString());
            log.info("doc_count:" + el.getDocCount());

        });
    }

    @ApiOperation("词频统计term vectors")
    @GetMapping("termVectors")
    public void term_vectors() throws IOException {
        TermVectorsRequest request = new TermVectorsRequest("my-index-000001", "1");
        request.setFieldStatistics(true);
        request.setTermStatistics(true);
        request.setPositions(true);
        request.setOffsets(true);
        request.setPayloads(true);

        TermVectorsResponse response =
                client.termvectors(request, RequestOptions.DEFAULT);

        for (TermVectorsResponse.TermVector tv : response.getTermVectorsList()) {
            String fieldname = tv.getFieldName();
            int docCount = tv.getFieldStatistics().getDocCount();
            long sumTotalTermFreq =
                    tv.getFieldStatistics().getSumTotalTermFreq();
            long sumDocFreq = tv.getFieldStatistics().getSumDocFreq();
            if (tv.getTerms() != null) {
                List<TermVectorsResponse.TermVector.Term> terms =
                        tv.getTerms();
                for (TermVectorsResponse.TermVector.Term term : terms) {
                    String termStr = term.getTerm();
                    System.out.println("termStr = " + termStr);
                    int termFreq = term.getTermFreq();
                    int docFreq = term.getDocFreq();
                    System.out.println("docFreq = " + docFreq);
                    long totalTermFreq = term.getTotalTermFreq();
//                    float score = term.getScore();
                    if (term.getTokens() != null) {
                        List<TermVectorsResponse.TermVector.Token> tokens =
                                term.getTokens();
                        for (TermVectorsResponse.TermVector.Token token : tokens) {
                            int position = token.getPosition();
                            int startOffset = token.getStartOffset();
                            int endOffset = token.getEndOffset();
                            String payload = token.getPayload();
                        }
                    }
                }
            }
        }
    }

    /*@ApiOperation("通过ElasticsearchRestTemplate实现词频统计")
    @GetMapping("aggregations")
    public void aggregations(){
        TermsAggregationBuilder aggregation = AggregationBuilders.terms("person-aggregate")
                .field("firstname");
        Query query = new NativeSearchQueryBuilder()
                .addAggregation(aggregation)
                .build();
        SearchHits<User> searchHits = elasticsearchRestTemplate.search(query,
                User.class);
        Aggregations aggregations = searchHits.getAggregations();
        Terms byCompanyAggregation = aggregations.get("person-aggregate");
        List<? extends Terms.Bucket> elasticBucket = byCompanyAggregation.getBuckets();
        elasticBucket.forEach(el -> {
            log.info("key:" + el.getKeyAsString());
            log.info("doc_count:" + el.getDocCount());

        });
    }*/

}
