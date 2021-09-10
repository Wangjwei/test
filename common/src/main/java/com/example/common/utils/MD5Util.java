package com.example.common.utils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.DigestUtils;

@Api("MD5工具类")
public class MD5Util {
    //盐，用于混交md5
    private static final String slat = "&%5123***&&%%$$#@";

    @ApiOperation("生成md5")
    public static String getMD5(String str) {
        String base = str +"/"+slat;
        return DigestUtils.md5DigestAsHex(base.getBytes());
    }
}
