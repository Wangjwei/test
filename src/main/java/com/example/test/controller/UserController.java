package com.example.test.controller;


import com.alibaba.fastjson.JSONObject;
import com.example.test.config.Result;
import com.example.test.entity.User;
import com.example.test.service.UserService;
import com.example.test.utils.ESClient;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
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
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery("ha","address", "name", "address.pinyin", "name.pinyin")) //同时对多个字段进行模糊匹配
                // 设置当前查询的超时时间
                .timeout(new TimeValue(60, TimeUnit.SECONDS))
                // 设置查询结果的页大小，默认是10
                .size(5)
                //搜索结果突出
                .highlighter(new HighlightBuilder()
                        /*.field("name")//搜索字段
                        .field("name.pinyin")
                        .field("address")*/
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

}

