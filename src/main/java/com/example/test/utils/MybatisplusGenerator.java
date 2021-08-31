package com.example.test.utils;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

/**
 * @author d
 * @ClassName: CodeGeneration
 * @Description: 代码生成器
 */
public class MybatisplusGenerator {
    /**
     * @Title: main
     * @Description: 生成
     */
    public static void main(String[] args) {
        //代码生成器
        AutoGenerator mpg = new AutoGenerator();
        //全局配置
        GlobalConfig gc = new GlobalConfig();
        // 代码生成的目录
        String projectPath = "E:\\workspace\\test";
        // 拼接出代码最终输出的目录
        gc.setOutputDir(projectPath + "/src/main/java");
        // 重新生成文件时是否覆盖，false 表示不覆盖
        gc.setFileOverride(true);
        //不需要ActiveRecord特性的请改为false
        gc.setActiveRecord(true);
        // 实体属性 Swagger2 注解，添加 Swagger 依赖，开启 Swagger2 模式
        gc.setSwagger2(true);
        // 配置是否打开目录，false 为不打开
        gc.setOpen(false);
        // 作者
        gc.setAuthor("wangjw");

        //自定义文件命名，注意%s 会自动填充表实体属性
        gc.setControllerName("%sController");
        gc.setServiceName("%sService");
        gc.setServiceImplName("%sServiceImpl");
        //gc.setEntityName("%sPo");
        gc.setMapperName("%sMapper");

        mpg.setGlobalConfig(gc);

        //数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDbType(DbType.MYSQL);
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("s_root");
        dsc.setPassword("1Q2w3e");
        dsc.setUrl("jdbc:mysql://192.168.3.126:3306/test?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC");
        mpg.setDataSource(dsc);

        //策略配置
        StrategyConfig strategy = new StrategyConfig();
        //此处可以修改您的表前缀
        strategy.setTablePrefix(new String[]{});
        //表名生成策略 underline_to_camel转驼峰命名，no_change默认的没变化
        strategy.setNaming(NamingStrategy.underline_to_camel);
        //列名生成策略 underline_to_camel转驼峰命名，no_change默认的没变化
        //strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        // 是否使用Lombok优化代码
        strategy.setEntityLombokModel(true);
        //需要生成的表
        strategy.setInclude(new String[]{"user"});

        strategy.setSuperServiceClass(null);
        strategy.setSuperServiceImplClass(null);

        strategy.setSuperMapperClass(null);
        strategy.setControllerMappingHyphenStyle(true);

        strategy.setEntityLombokModel(true);
        //strategy.setEntitySerialVersionUID(true);
        //strategy.setEntityTableFieldAnnotationEnable(true);

        mpg.setStrategy(strategy);
        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();
        templateConfig.setXml(null);
        mpg.setTemplate(templateConfig);

        //包配置
        PackageConfig pc = new PackageConfig();
        // 配置父包名
        pc.setParent("com.example.test");
        // 配置模块名
        //pc.setModuleName("member");
        // 配置 controller 包名
        pc.setController("controller");
        // 配置 service 包名
        pc.setService("service");
        // 配置 serviceImpl 包名
        pc.setServiceImpl("service.impl");
        // 配置 mapper 包名
        pc.setMapper("service.mapper");
        // 配置 entity 包名
        pc.setEntity("entity");
        mpg.setPackageInfo(pc);

        //执行生成
        mpg.execute();
    }

}
