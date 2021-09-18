package com.example.admin.controller;

import com.example.admin.config.QuartzManager;
import com.example.admin.config.Result;
import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/quartz")
public class QuartzController {
    @Resource
    private QuartzManager quartzManager;

    @GetMapping("startJob")
    public Result startJob(@RequestParam("name") String name,   //job的名称
                           @RequestParam("group") String group,//job的组
                           @RequestParam("corn") String corn   //corn表达式
    ) throws SchedulerException {
        quartzManager.startJob(name, group, corn);
        String s = quartzManager.getjobInfo(name, group);
        System.out.println("job:" + s);
        return Result.ok();
    }

    @GetMapping("pauseJob")
    public Result pauseJob(@RequestParam String name, @RequestParam String group) throws SchedulerException {
        quartzManager.pauseJob(name,group);
        String s = quartzManager.getjobInfo(name, group);
        System.out.println("job:" + s);
        return Result.ok();
    }

}
