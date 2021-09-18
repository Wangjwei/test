package com.example.admin.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.admin.config.Result;
import com.example.admin.entity.User;
import com.example.admin.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

}

