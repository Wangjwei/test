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
    @ApiOperation("mybatis-plus分页")
    void testPage(){
        //配置分页条件，当前页，每页数量
        Page<User> userPage = new Page<>(2, 2);
        //分页查询数据
        IPage<User> page = userService.page(userPage, new QueryWrapper<User>().like("url", "www"));
        log.info("数据总条数：{},总页数：{}",page.getTotal(),page.getPages());
        List<User> userList = page.getRecords();
        userList.forEach(user -> log.info(JSONObject.toJSONString(user)));
    }


    //ES

    @ApiOperation("创建Index")
    @Test
    void createIndex() throws IOException {
        // 1、创建索引请求(必须是英文字母小写，且不含中划线)
        CreateIndexRequest request = new CreateIndexRequest("user");
        // 2、客户端执行请求 IndicesClient,请求后获得响应
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse);
    }

    @ApiOperation("删除Index")
    @Test
    void testDeleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("user");
        // 删除
        AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());
        client.close();
    }

    @ApiOperation("判断Index存在")
    @Test
    void testExistIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("user");
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
        client.close();
    }

    @ApiOperation("创建Document")
    @Test
    void testAddDocument() throws IOException {
        //创建对象
        User user = User.builder().name("张三").url("www.baidu.com").age(25).sex(1).address("打发士大夫和地方").build();
        //创建请求
        IndexRequest request = new IndexRequest("user");
        //规则 put /user/_doc/1
        request.id("1");
        //设置超时时间
        request.timeout(TimeValue.timeValueSeconds(1));
        //request.timeout("1s");
        //将我们的数据放入请求 json
        request.source(JSON.toJSONString(user), XContentType.JSON);
        //客户端发送请求 , 获取响应的结果
        IndexResponse indexResponse = client.index(request,
                RequestOptions.DEFAULT);
        System.out.println(indexResponse.toString()); //打印文档的内容
        System.out.println(indexResponse.status()); // 对应我们命令返回的状态 ,第一次创建，返回CREATED
        client.close();
    }

    @ApiOperation("判断Document存在")
    @Test
    void testIsExists() throws IOException {
        GetRequest getRequest = new GetRequest("user", "1");
        boolean exists = client.exists(getRequest, RequestOptions.DEFAULT);
        System.out.println(exists);
        client.close();
    }

    @ApiOperation("获取Document")
    @Test
    void testGetDocument() throws IOException {
        GetRequest getRequest = new GetRequest("user", "1");
        GetResponse getResponse = client.get(getRequest,
                RequestOptions.DEFAULT);
        System.out.println(getResponse.getSourceAsString()); // 打印文档的内容
        System.out.println(getResponse); // 返回的全部内容是和命令式一样的
        client.close();
    }

    @ApiOperation("更新Document")
    @Test
    void testUpdateRequest() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("user", "3");
        updateRequest.timeout("1s");
        User user = User.builder().name("张三一").url("www.7k7k.com").age(26).sex(0).phone("13888888825").address("是否活动费活动费25").build();
        updateRequest.doc(JSON.toJSONString(user), XContentType.JSON);
        UpdateResponse updateResponse = client.update(updateRequest,
                RequestOptions.DEFAULT);
        System.out.println(updateResponse.status());
        client.close();
    }

    @ApiOperation("删除Document")
    @Test
    void testDeleteRequest() throws IOException {
        DeleteRequest request = new DeleteRequest("user", "1");
        request.timeout("1s");
        DeleteResponse deleteResponse = client.delete(request,
                RequestOptions.DEFAULT);
        System.out.println(deleteResponse.status());
        client.close();
    }

    @ApiOperation("批量插入Document")
    @Test
    void testBulkRequest() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");
        ArrayList<User> userList = new ArrayList<User>();
        userList.add(User.builder().name("张三").url("www.baidu.com").age(25).sex(1).phone("13804518289").address("哈尔滨").build());
        userList.add(User.builder().name("李四").url("www.7k7k.com").age(26).sex(0).phone("13999665213").address("哈尔滨，南岗").build());
        userList.add(User.builder().name("王五十").url("www.546.com").age(27).sex(1).phone("13145670582").address("哈尔滨 南岗区 十字街 三十李四号").build());
        userList.add(User.builder().name("赵六").url("www.三十.com").age(28).sex(0).phone("15145730826").address("哈尔滨-南岗区-十字街-五十二号").build());
        userList.add(User.builder().name("十七").url("www.4568769.com").age(29).sex(1).phone("15904518289").address("哈尔滨香坊区十字街五十二号").build());
        for (int i = 0; i < userList.size(); i++) {
            // 批量更新和批量删除，就在这里修改对应的请求就可以了
            bulkRequest.add(new IndexRequest("user").id("" + (i + 1))
                    .source(JSON.toJSONString(userList.get(i)), XContentType.JSON));
        }
        BulkResponse bulkResponse = client.bulk(bulkRequest,
                RequestOptions.DEFAULT);
        System.out.println(bulkResponse.hasFailures()); // 是否失败，返回fals代表成功
        client.close();
    }

    @ApiOperation("查询全部")
    @Test
    void testSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest("user");
        // 构建搜索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // QueryBuilders.matchAllQuery() 匹配全部文档
        MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
        sourceBuilder.query(matchAllQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest,
                RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();

        System.out.println(hits.getTotalHits());
        System.out.println(searchResponse.getTook());

        //数据遍历出来
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
        client.close();
    }

    @ApiOperation("模糊查询")
    @Test
    void fuzzyQuery() throws IOException {
        SearchRequest request = new SearchRequest();
        request.scroll(new TimeValue(1, TimeUnit.HOURS)); //滚动游标保留多久
        request.setBatchedReduceSize(10);//每批次拉多少条
        request.indices("user");

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //仅支持中文
        MatchQueryBuilder queryBuilder = new MatchQueryBuilder("address", "大")
                // 设置查询前缀长度
                //.prefixLength(3)
                // 设置模糊查询最大扩展
                //.maxExpansions(10)
                // 开启模糊查询
                .fuzziness(Fuzziness.AUTO);

        //term搜索
        //term 根据检索词来准确匹配字段(不要用term去搜索text类型的字段)
//        TermQueryBuilder queryBuilder = QueryBuilders.termQuery("name", "张三");
        //terms 相当于or的关系，只要一个匹配就行
        //TermsQueryBuilder queryBuilder = QueryBuilders.termsQuery("name","张","三");
        //range 对字段进行范围的匹配 gt 大于 lt 小于
        //RangeQueryBuilder queryBuilder = QueryBuilders.rangeQuery("age").gt(26).lt(29);
        //prefix 返回所有包含以检索词为前缀的字段的文档
        //PrefixQueryBuilder queryBuilder = QueryBuilders.prefixQuery("name", "张三");
        //wildcard 通配符匹配，返回匹配包含通配符的检索词的结果(?：匹配任何单一的字符;*：匹配0个或者多个字符)(最好避免在检索词的开头使用*或者?，这会降低搜索性能)
        //WildcardQueryBuilder queryBuilder =QueryBuilders.wildcardQuery("name","十*");

        //text搜索
        //match 查找和检索词短语匹配的文档(检索词可以是文本、数字、日期或者布尔值) 也可以进行模糊匹配
        //MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery("address","香坊");
        //match_bool_prefix 解析检索词，生成一个bool复合检索语句
        //MatchBoolPrefixQueryBuilder queryBuilder = QueryBuilders.matchBoolPrefixQuery("address", "南 岗");
        //multi_match 同时对多个字段进行查询匹配
        //MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery("四","address", "name", "address.pinyin", "name.pinyin");

        sourceBuilder.query(queryBuilder);
        sourceBuilder.timeout(TimeValue.timeValueMinutes(2L));
        //sourceBuilder.size(10);//分页量
        //sourceBuilder.sort("name", SortOrder.DESC);//排序

        System.out.println(sourceBuilder.toString());
        request.source(sourceBuilder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();

        System.out.println(hits.getTotalHits());
        System.out.println(response.getTook());

        //数据遍历出来
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
        client.close();
    }

    @ApiOperation("拼音搜索")
    @Test
    void pinyinSearch() throws IOException {
        SearchRequest request = new SearchRequest();
        //request.scroll(new TimeValue(1, TimeUnit.HOURS)); //滚动游标保留多久
        //request.setBatchedReduceSize(10);//每批次拉多少条
        request.indices("user");

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        WildcardQueryBuilder queryBuilder = QueryBuilders.wildcardQuery("name.pinyin", "ls"+"*");
        WildcardQueryBuilder queryBuilder = QueryBuilders.wildcardQuery("address.pinyin", "xiangf"+"*");

        /*//使用dis_max直接取多个query中，分数最高的那一个query的分数即可
        DisMaxQueryBuilder queryBuilder = QueryBuilders.disMaxQuery();
        //boost 设置权重,只搜索匹配name和address字段
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

        //数据遍历出来
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
        client.close();
    }

    /*@ApiOperation("模糊查询")
    @Test
    void boolQueryBuilder() throws IOException {
        //should条件 类似于or
        //1、全文检索  key
        // bool的Should方式查询
        SearchRequest request = new SearchRequest();
        request.scroll(new TimeValue(1, TimeUnit.HOURS)); //滚动游标保留多久
        request.setBatchedReduceSize(10);//每批次拉多少条
        request.indices("user");

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        String key = "十";
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

        //数据遍历出来
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
        }
    }*/

    /*@ApiOperation("模糊查询")
    @Test
    void multiMatch() throws IOException {
        SearchRequest request = new SearchRequest();
        request.scroll(new TimeValue(1, TimeUnit.HOURS)); //滚动游标保留多久
        request.setBatchedReduceSize(10);//每批次拉多少条
        request.indices("user");

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        String key = "十";
        //1、全文检索  key
        // MultiMatch方式查询
        if(StringUtils.isNotBlank(key)){
            //设置字段权重
            Map<String, Float> fields = new HashMap<String, Float>();
            fields.put("name", 5f);
            fields.put("address", 5f);
            fields.put("phone", 2f);
            fields.put("url", 1f);
            fields.put("age", 1f);
            fields.put("sex", 1f);
            //查询
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

        //数据遍历出来
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

            //发送验证码，假设生成的验证码
            code = new StringBuffer();
            for (int i = 0; i < 6; i++) {
                code.append(new Random().nextInt(10));
            }
            log.info("code:{},now:{}", code, LocalDateTime.now());
            //缓存中添加验证码
            jedis.setex(tel, Long.valueOf(60 * 2), code.toString());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //关闭redis
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
