package com.example.admin.config;

import io.swagger.annotations.Api;

@Api("返回结果bean封装")
public class Result {
    public static final String SUCCESS = "操作成功。";
    public static final String FAILURE = "操作失败！";

    private boolean result;
    private String msg;
    private Object datas;

    private Result() {}

    public static Result ok() {
        return Result.ok(SUCCESS);
    }

    public static Result ok(String msg) {
        return Result.ok(msg, null);
    }

    public static Result ok(Object datas) {
        return Result.ok(SUCCESS, datas);
    }

    public static Result ok(String msg, Object datas) {
        Result result = new Result();
        result.setResult(true);
        result.setMsg(msg);
        result.setDatas(datas);
        return result;
    }

    public static Result fail() {
        return Result.fail(FAILURE);
    }

    public static Result fail(String msg) {
        return Result.fail(msg, null);
    }

    public static Result fail(Object datas) {
        return Result.fail(FAILURE, datas);
    }

    public static Result fail(String msg, Object datas) {
        Result result = new Result();
        result.setResult(false);
        result.setMsg(msg);
        result.setDatas(datas);
        return result;
    }

    public boolean isResult() {
        return result;
    }
    public void setResult(boolean result) {
        this.result = result;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public Object getDatas() {
        return datas;
    }
    public void setDatas(Object datas) {
        this.datas = datas;
    }
}
