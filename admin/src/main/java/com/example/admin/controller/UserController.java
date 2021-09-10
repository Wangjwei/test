package com.example.admin.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.alibaba.nacos.api.naming.NamingService;
import com.example.admin.entity.User;
import com.example.admin.service.UserService;
import com.example.common.config.Result;
import com.example.common.utils.RedisUtils;
import com.example.elasticsearch.utils.ESClient;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author wangjw
 * @since 2021-09-01
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @NacosInjected
    private NamingService namingService;
    @NacosValue(value = "${wjwTest:test}", autoRefreshed = true)
    private String wjwTest;
    /*@Value("${nacos.config.server-addr}")
    private String serverAddr;
    @Value("${nacos.config.namespace}")
    private String namespace;
    @Value("nacos.config.data-id")
    private String dataId;
    @Value("nacos.config.group:DEFAULT_GROUP")
    private String group;*/

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/nacos")
    public Result nacos(){
        /*//根据ip和命名空间获取配置文件对象ConfigService
        Properties properties = new Properties();
        properties.put("serverAddr", serverAddr);
        properties.put("namespace", namespace);
        ConfigService configService = NacosFactory.createConfigService(properties);
        //获得配置内容
        String content = configService.getConfig(dataId, group, 5000);
        log.info("content:{}",content);
        //发布配置
        String config = "wjwMsg: myNacos";
        configService.publishConfig(dataId, group, config);
        //删除配置
        configService.removeConfig(dataId, group);
        //添加监听
        Listener listener = new Listener() {
            @Override
            public void receiveConfigInfo(String configInfo) {
                System.out.println("变更后读取到的配置内容：" + "\r\n" + configInfo);
            }
            @Override
            public Executor getExecutor() {
                return null;
            }
        };
        configService.addListener(dataId,group,listener);
        //删除监听
        configService.removeListener(dataId, group, listener);*/

        //查询服务列表
        //namingService.getAllInstances(serviceName) ;
        return Result.ok(wjwTest);
        //return Result.ok();
    }

    @ApiOperation("新增")
    @PostMapping("/add")
    public Result add() {
        User user = User.builder().name("张三").url("www.baidu.com").age(25).sex(1).phone("15104518289").address("哈尔滨南岗区十字街三十五号").build();
        if (userService.save(user)) return Result.ok("添加成功！");
        else return Result.fail("添加失败");
    }

    @ApiOperation("删除")
    @DeleteMapping("/del")
    public Result delete(Integer id) {
        User user = userService.getById(id);
        if (ObjectUtils.isNotEmpty(user)) {
            userService.removeById(id);
            return Result.ok("删除成功！");
        } else {
            return Result.fail("参数传输错误！");
        }
    }

    @ApiOperation("修改")
    @PutMapping("/update")
    public Result update(@RequestBody User user) {
        User u = userService.getById(user.getId());
        if (ObjectUtils.isNotEmpty(u)) {
            userService.saveOrUpdate(user);
            log.info("修改结果" + JSONObject.toJSONString(user));
            return Result.ok("修改成功！");
        } else {
            log.error("参数传输错误:" + JSONObject.toJSONString(user));
            return Result.fail("参数传输错误！");
        }
    }

    @ApiOperation("查询")
    @GetMapping("/get/{id}")
    public Result query(@PathVariable Integer id) {
        User user = userService.getById(id);
        log.info("查询结果" + JSONObject.toJSONString(user));
        if (ObjectUtils.isNotEmpty(user)) return Result.ok(user);
        else return Result.fail("参数传输错误！");
    }

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
        RestHighLevelClient client = ESClient.getClient();
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


    @GetMapping("redisTest")
    public Result redisTest(@RequestParam String tel){
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
            jedis.setex(tel, 60 * 2, code.toString());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //关闭redis
            assert jedis != null;
            jedis.close();
        }
        return Result.ok(code);
    }

    @GetMapping("checkValidation")
    public Result checkValidation(@RequestParam String code,@RequestParam String tel) {
        log.info("code:{},tel:{}",code,tel);
        Jedis jedis = RedisUtils.getRedis();
        boolean flag = false;
        try {
            assert jedis != null;
            String validation = jedis.get(tel);
            log.info("validation:{}",validation);
            flag = validation.equals(code);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assert jedis != null;
            jedis.close();
        }
        if (flag) return Result.ok("成功");else return Result.fail("失败");
    }

}

