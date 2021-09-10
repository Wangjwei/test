package com.example.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.admin.entity.User;
import io.swagger.annotations.ApiOperation;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author wangjw
 * @since 2021-09-10
 */
public interface UserService extends IService<User> {

    @ApiOperation("插入数据到索引中")
    void insertUser() throws Exception;

    @ApiOperation("根据用户输入内容获取自动补全提示语")
    List<String> searchCompletionSuggest(String searchValue);

}
