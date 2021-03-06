package com.example.admin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.admin.entity.User;
import com.example.admin.service.UserService;
import com.example.admin.utils.RedisUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootTest
class AdminApplicationTests {

    private final RestHighLevelClient client;

    private final UserService userService;

    @Autowired
    AdminApplicationTests(RestHighLevelClient client, UserService userService) {
        this.client = client;
        this.userService = userService;
    }

    @Test
    void contextLoads() {
    }

    @Test
    @ApiOperation("mybatis-plus??????")
    void testPage(){
        //?????????????????????????????????????????????
        Page<User> userPage = new Page<>(2, 2);
        //??????????????????
        IPage<User> page = userService.page(userPage, new QueryWrapper<User>().like("url", "www"));
        log.info("??????????????????{},????????????{}",page.getTotal(),page.getPages());
        List<User> userList = page.getRecords();
        userList.forEach(user -> log.info(JSONObject.toJSONString(user)));
    }


    //ES

    @ApiOperation("??????Index")
    @Test
    void createIndex() throws IOException {
        // 1?????????????????????(????????????????????????????????????????????????)
        CreateIndexRequest request = new CreateIndexRequest("user");
        // 2???????????????????????? IndicesClient,?????????????????????
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse);
    }

    @ApiOperation("??????Index")
    @Test
    void testDeleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("user");
        // ??????
        AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());
        client.close();
    }

    @ApiOperation("??????Index??????")
    @Test
    void testExistIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("user");
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
        client.close();
    }

    @ApiOperation("??????Document")
    @Test
    void testAddDocument() throws IOException {
        //????????????
        User user = User.builder().name("??????").url("www.baidu.com").age(25).sex(1).address("????????????????????????").build();
        //????????????
        IndexRequest request = new IndexRequest("user");
        //?????? put /user/_doc/1
        request.id("1");
        //??????????????????
        request.timeout(TimeValue.timeValueSeconds(1));
        //request.timeout("1s");
        //?????????????????????????????? json
        request.source(JSON.toJSONString(user), XContentType.JSON);
        //????????????????????? , ?????????????????????
        IndexResponse indexResponse = client.index(request,
                RequestOptions.DEFAULT);
        System.out.println(indexResponse.toString()); //?????????????????????
        System.out.println(indexResponse.status()); // ????????????????????????????????? ,????????????????????????CREATED
        client.close();
    }

    @ApiOperation("??????Document??????")
    @Test
    void testIsExists() throws IOException {
        GetRequest getRequest = new GetRequest("user", "1");
        boolean exists = client.exists(getRequest, RequestOptions.DEFAULT);
        System.out.println(exists);
        client.close();
    }

    @ApiOperation("??????Document")
    @Test
    void testGetDocument() throws IOException {
        GetRequest getRequest = new GetRequest("user", "1");
        GetResponse getResponse = client.get(getRequest,
                RequestOptions.DEFAULT);
        System.out.println(getResponse.getSourceAsString()); // ?????????????????????
        System.out.println(getResponse); // ?????????????????????????????????????????????
        client.close();
    }

    @ApiOperation("??????Document")
    @Test
    void testUpdateRequest() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("user", "3");
        updateRequest.timeout("1s");
        User user = User.builder().name("?????????").url("www.7k7k.com").age(26).sex(0).phone("13888888825").address("????????????????????????25").build();
        updateRequest.doc(JSON.toJSONString(user), XContentType.JSON);
        UpdateResponse updateResponse = client.update(updateRequest,
                RequestOptions.DEFAULT);
        System.out.println(updateResponse.status());
        client.close();
    }

    @ApiOperation("??????Document")
    @Test
    void testDeleteRequest() throws IOException {
        DeleteRequest request = new DeleteRequest("user", "1");
        request.timeout("1s");
        DeleteResponse deleteResponse = client.delete(request,
                RequestOptions.DEFAULT);
        System.out.println(deleteResponse.status());
        client.close();
    }

    @ApiOperation("????????????Document")
    @Test
    void testBulkRequest() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");
        ArrayList<User> userList = new ArrayList<User>();
        userList.add(User.builder().name("??????").url("www.baidu.com").age(25).sex(1).phone("13804518289").address("?????????").build());
        userList.add(User.builder().name("??????").url("www.7k7k.com").age(26).sex(0).phone("13999665213").address("??????????????????").build());
        userList.add(User.builder().name("?????????").url("www.546.com").age(27).sex(1).phone("13145670582").address("????????? ????????? ????????? ???????????????").build());
        userList.add(User.builder().name("??????").url("www.??????.com").age(28).sex(0).phone("15145730826").address("?????????-?????????-?????????-????????????").build());
        userList.add(User.builder().name("??????").url("www.4568769.com").age(29).sex(1).phone("15904518289").address("???????????????????????????????????????").build());
        for (int i = 0; i < userList.size(); i++) {
            // ???????????????????????????????????????????????????????????????????????????
            bulkRequest.add(new IndexRequest("user").id("" + (i + 1))
                    .source(JSON.toJSONString(userList.get(i)), XContentType.JSON));
        }
        BulkResponse bulkResponse = client.bulk(bulkRequest,
                RequestOptions.DEFAULT);
        System.out.println(bulkResponse.hasFailures()); // ?????????????????????fals????????????
        client.close();
    }

    @ApiOperation("????????????")
    @Test
    void testSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest("user");
        // ??????????????????
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // QueryBuilders.matchAllQuery() ??????????????????
        MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
        sourceBuilder.query(matchAllQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest,
                RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();

        System.out.println(hits.getTotalHits());
        System.out.println(searchResponse.getTook());

        //??????????????????
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
        client.close();
    }

    @ApiOperation("????????????")
    @Test
    void fuzzyQuery() throws IOException {
        SearchRequest request = new SearchRequest();
        request.scroll(new TimeValue(1, TimeUnit.HOURS)); //????????????????????????
        request.setBatchedReduceSize(10);//?????????????????????
        request.indices("user");

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //???????????????
        MatchQueryBuilder queryBuilder = new MatchQueryBuilder("address", "???")
                // ????????????????????????
                //.prefixLength(3)
                // ??????????????????????????????
                //.maxExpansions(10)
                // ??????????????????
                .fuzziness(Fuzziness.AUTO);

        //term??????
        //term ????????????????????????????????????(?????????term?????????text???????????????)
//        TermQueryBuilder queryBuilder = QueryBuilders.termQuery("name", "??????");
        //terms ?????????or????????????????????????????????????
        //TermsQueryBuilder queryBuilder = QueryBuilders.termsQuery("name","???","???");
        //range ?????????????????????????????? gt ?????? lt ??????
        //RangeQueryBuilder queryBuilder = QueryBuilders.rangeQuery("age").gt(26).lt(29);
        //prefix ?????????????????????????????????????????????????????????
        //PrefixQueryBuilder queryBuilder = QueryBuilders.prefixQuery("name", "??????");
        //wildcard ??????????????????????????????????????????????????????????????????(???????????????????????????????;*?????????0?????????????????????)(???????????????????????????????????????*??????????????????????????????????)
        //WildcardQueryBuilder queryBuilder =QueryBuilders.wildcardQuery("name","???*");

        //text??????
        //match ???????????????????????????????????????(?????????????????????????????????????????????????????????) ???????????????????????????
        //MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery("address","??????");
        //match_bool_prefix ??????????????????????????????bool??????????????????
        //MatchBoolPrefixQueryBuilder queryBuilder = QueryBuilders.matchBoolPrefixQuery("address", "??? ???");
        //multi_match ???????????????????????????????????????
        //MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery("???","address", "name", "address.pinyin", "name.pinyin");

        sourceBuilder.query(queryBuilder);
        sourceBuilder.timeout(TimeValue.timeValueMinutes(2L));
        //sourceBuilder.size(10);//?????????
        //sourceBuilder.sort("name", SortOrder.DESC);//??????

        System.out.println(sourceBuilder.toString());
        request.source(sourceBuilder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();

        System.out.println(hits.getTotalHits());
        System.out.println(response.getTook());

        //??????????????????
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
        client.close();
    }

    @ApiOperation("????????????")
    @Test
    void pinyinSearch() throws IOException {
        SearchRequest request = new SearchRequest();
        //request.scroll(new TimeValue(1, TimeUnit.HOURS)); //????????????????????????
        //request.setBatchedReduceSize(10);//?????????????????????
        request.indices("user");

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        WildcardQueryBuilder queryBuilder = QueryBuilders.wildcardQuery("name.pinyin", "ls"+"*");
        WildcardQueryBuilder queryBuilder = QueryBuilders.wildcardQuery("address.pinyin", "xiangf"+"*");

        /*//??????dis_max???????????????query??????????????????????????????query???????????????
        DisMaxQueryBuilder queryBuilder = QueryBuilders.disMaxQuery();
        //boost ????????????,???????????????name???address??????
        QueryBuilder ikNameQuery = QueryBuilders.matchQuery("name", "san").boost(2f);
        QueryBuilder pinyinNameQuery = QueryBuilders.matchQuery("name.pinyin", "san");
        QueryBuilder ikDirectorQuery = QueryBuilders.matchQuery("address", "san").boost(2f);
        queryBuilder.add(ikNameQuery);
        queryBuilder.add(pinyinNameQuery);
        queryBuilder.add(ikDirectorQuery);*/

        sourceBuilder.query(queryBuilder);
        sourceBuilder.timeout(TimeValue.timeValueMinutes(2L));

        System.out.println(sourceBuilder.toString());
        request.source(sourceBuilder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();

        System.out.println(hits.getTotalHits());
        System.out.println(response.getTook());

        //??????????????????
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
        client.close();
    }

    /*@ApiOperation("????????????")
    @Test
    void boolQueryBuilder() throws IOException {
        //should?????? ?????????or
        //1???????????????  key
        // bool???Should????????????
        SearchRequest request = new SearchRequest();
        request.scroll(new TimeValue(1, TimeUnit.HOURS)); //????????????????????????
        request.setBatchedReduceSize(10);//?????????????????????
        request.indices("user");

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        String key = "???";
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        BoolQueryBuilder boolShouldQueryBuilder=new BoolQueryBuilder();
        if(StringUtils.isNotBlank(key)){
            System.out.println(">>>>>>>>>>>>>>>>>>>>");
            boolShouldQueryBuilder.should(new MatchQueryBuilder("name", key).boost(1f));
            boolShouldQueryBuilder.should(new MatchQueryBuilder("address", key).boost(1f));
            boolShouldQueryBuilder.should(new MatchQueryBuilder("phone", key).boost(0.5f));
            boolShouldQueryBuilder.should(new MatchQueryBuilder("url", key).boost(0.2f));
            boolShouldQueryBuilder.should(new MatchQueryBuilder("sex", key).boost(0.2f));
            boolShouldQueryBuilder.should(new MatchQueryBuilder("age", key).boost(0.2f));
        }else{
            boolShouldQueryBuilder.should(new MatchAllQueryBuilder());
        }
        boolQueryBuilder.must(boolShouldQueryBuilder);
        request.source(sourceBuilder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();

        System.out.println(hits.getTotalHits());
        System.out.println(response.getTook());

        //??????????????????
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
    }*/

    /*@ApiOperation("????????????")
    @Test
    void multiMatch() throws IOException {
        SearchRequest request = new SearchRequest();
        request.scroll(new TimeValue(1, TimeUnit.HOURS)); //????????????????????????
        request.setBatchedReduceSize(10);//?????????????????????
        request.indices("user");

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        String key = "???";
        //1???????????????  key
        // MultiMatch????????????
        if(StringUtils.isNotBlank(key)){
            //??????????????????
            Map<String, Float> fields = new HashMap<String, Float>();
            fields.put("name", 5f);
            fields.put("address", 5f);
            fields.put("phone", 2f);
            fields.put("url", 1f);
            fields.put("age", 1f);
            fields.put("sex", 1f);
            //??????
            MultiMatchQueryBuilder multiMatchQueryBuilder = new MultiMatchQueryBuilder(key,"name", "address", "phone", "url", "age","sex").fields(fields);
            boolQueryBuilder.must(multiMatchQueryBuilder);
        }else{
            boolQueryBuilder.must(new MatchAllQueryBuilder());
        }
        request.source(sourceBuilder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();

        System.out.println(hits.getTotalHits());
        System.out.println(response.getTook());

        //??????????????????
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
    }*/




    void redisTest(){
        String tel = "13145678289";
        Jedis jedis = RedisUtils.getRedis();
        StringBuffer code = null;
        try {
            assert jedis != null;
            jedis.select(0);

            //??????????????????????????????????????????
            code = new StringBuffer();
            for (int i = 0; i < 6; i++) {
                code.append(new Random().nextInt(10));
            }
            log.info("code:{},now:{}", code, LocalDateTime.now());
            //????????????????????????
            jedis.setex(tel, Long.valueOf(60 * 2), code.toString());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //??????redis
            assert jedis != null;
            jedis.close();
        }
    }

    void checkValidation() {
        String code = "123456";
        String tel = "13145678289";
        log.info("code:{},tel:{}",code,tel);
        Jedis jedis = RedisUtils.getRedis();
        try {
            assert jedis != null;
            String validation = jedis.get(tel);
            log.info("validation:{}",validation);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assert jedis != null;
            jedis.close();
        }
    }

}
