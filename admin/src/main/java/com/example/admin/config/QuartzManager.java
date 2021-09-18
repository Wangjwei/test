package com.example.admin.config;

import com.example.admin.job.MyJob;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Slf4j
@Configuration
public class QuartzManager {

    @ApiModelProperty("任务调度")
    private Scheduler scheduler;

    @Bean
    public Scheduler scheduler() throws SchedulerException {
        SchedulerFactory schedulerFactoryBean = new StdSchedulerFactory();
        this.scheduler = schedulerFactoryBean.getScheduler();
        return schedulerFactoryBean.getScheduler();
    }

    @ApiOperation("获取所有Job")
    private void getAll(){
    }

    @ApiOperation("开始执行定时任务")
    public void startJob(String name, String group, String corn) throws SchedulerException {
        startJobTask(scheduler, name, group, corn);
        scheduler.start();
    }

    @ApiOperation("启动定时任务")
    private void startJobTask(Scheduler scheduler, String name, String group, String corn) throws SchedulerException {
        //对于身份
        JobDetail jobDetail = JobBuilder.newJob(MyJob.class).withIdentity(name, group).storeDurably(true).requestRecovery(true).build();
        log.info("jobDetail:{}", jobDetail);

        // SimpleScheduleBuilder  CronScheduleBuilder  用于构建Scheduler，定义任务调度的时间规则
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(corn);
        //触发器
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().forJob(jobDetail).withIdentity(name, group)
                .withSchedule(cronScheduleBuilder).build();
        scheduler.scheduleJob(jobDetail, cronTrigger);

    }

    @ApiOperation("获取Job信息")
    public String getjobInfo(String name, String group) throws SchedulerException {
        TriggerKey triggerKey = new TriggerKey(name, group);
        log.info("triggerKey:" + triggerKey);
        CronTrigger cronTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        return String.format("time:%s,state:%s", cronTrigger.getCronExpression(),
                scheduler.getTriggerState(triggerKey).name());
    }

    @ApiOperation("修改任务的执行时间")
    public boolean modifyJob(String name, String group, String cron) throws SchedulerException {
        Date date = null;
        TriggerKey triggerKey = new TriggerKey(name, group);
        CronTrigger cronTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        String oldTime = cronTrigger.getCronExpression();
        if (!oldTime.equalsIgnoreCase(cron)) {
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(name, group)
                    .withSchedule(cronScheduleBuilder).build();
            date = scheduler.rescheduleJob(triggerKey, trigger);
        }
        return date != null;
    }

    @ApiOperation("暂停所有任务")
    public void pauseAllJob() throws SchedulerException {
        scheduler.pauseAll();
    }

    @ApiOperation("暂停某个任务")
    public void pauseJob(String name, String group) throws SchedulerException {
        JobKey jobKey = new JobKey(name, group);
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail == null) {
            return;
        }
        scheduler.pauseJob(jobKey);
    }

    @ApiOperation("恢复所有任务")
    public void resumeAllJob() throws SchedulerException {
        scheduler.resumeAll();
    }

    @ApiOperation("恢复某个任务")
    public void resumeJob(String name, String group) throws SchedulerException {
        JobKey jobKey = new JobKey(name, group);
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail == null) {
            return;
        }
        scheduler.resumeJob(jobKey);
    }

    @ApiOperation("删除某个任务")
    public void deleteJob(String name, String group) throws SchedulerException {
        JobKey jobKey = new JobKey(name, group);
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail == null) {
            return;
        }
        scheduler.deleteJob(jobKey);
    }

}
