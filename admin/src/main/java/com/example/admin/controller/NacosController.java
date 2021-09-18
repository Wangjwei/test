package com.example.admin.controller;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/nacos")
public class NacosController {

    @NacosValue(value = "${wjwTest:test}", autoRefreshed = true)
    private String wjwTest;

    @GetMapping("/get")
    public String nacos(){
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
        return wjwTest;
        //return Result.ok();
    }
}
