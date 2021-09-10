package com.example.admin;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.admin.entity.User;
import com.example.admin.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest
class AdminApplicationTests {

    private final UserService userService;

    @Autowired
    AdminApplicationTests(UserService userService) {
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

}
