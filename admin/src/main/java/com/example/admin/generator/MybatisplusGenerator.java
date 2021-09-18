package com.example.admin.generator;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

//代码生成器
public class MybatisplusGenerator {
    public static void main(String[] args) {
        //整合配置 全局配置+数据源配置+策略配置+包名策略配置
        AutoGenerator mpg = new AutoGenerator();
        // 选择 freemarker 引擎，默认 Velocity 需要在配置文件引入依赖
        //mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        //1全局配置
        GlobalConfig gc = new GlobalConfig();
        // 代码生成的目录
        // 拼接出代码最终输出的目录
        gc.setOutputDir("E://workspace/test/admin/src/main/java/");
        // 重新生成文件时是否覆盖，false 表示不覆盖
        gc.setFileOverride(true);
        gc.setIdType(IdType.AUTO);// 主键策略
        //不需要ActiveRecord特性的请改为false
        gc.setActiveRecord(true);
        //gc.setEnableCache(false);// XML 二级缓存
        //gc.setBaseResultMap(true);// XML ResultMap 生成基本的resultmap
        //gc.setBaseColumnList(false);// XML columList 生成基本的sql片段
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

        //2数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDbType(DbType.MYSQL);
        /*dsc.setTypeConvert(new MySqlTypeConvert() {
            // 自定义数据库表字段类型转换【可选】
            @Override
            public DbColumnType processTypeConvert(String fieldType) {
                System.out.println("转换类型：" + fieldType);
                // 注意！！processTypeConvert 存在默认类型转换，如果不是你要的效果请自定义返回、非如下直接返回。
                return super.processTypeConvert(fieldType);
            }
        });*/
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("s_root");
        dsc.setPassword("1Q2w3e");
        dsc.setUrl("jdbc:mysql://192.168.3.126:3306/quartz?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC");
        mpg.setDataSource(dsc);

        //3策略配置
        StrategyConfig strategy = new StrategyConfig();
        //此处可以修改您的表前缀
        //strategy.setTablePrefix(new String[]{"test_"});// 此处可以修改为您的表前缀
        //表名生成策略 underline_to_camel转驼峰命名，no_change默认的没变化
        strategy.setNaming(NamingStrategy.underline_to_camel);
        //列名生成策略 underline_to_camel转驼峰命名，no_change默认的没变化
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        // 是否使用Lombok优化代码
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);//controller以restFule风格
        // strategy.setCapitalMode(true);// 全局大写命名 ORACLE 注意
        //需要生成的表
        strategy.setInclude(new String[]{"sys_task"});
        // strategy.setExclude(new String[]{"test"}); // 排除生成的表
        // 自定义实体父类
        // strategy.setSuperEntityClass("com.baomidou.demo.TestEntity");
        // 自定义实体，公共字段
        // strategy.setSuperEntityColumns(new String[] { "test_id", "age" });
        // 自定义 service 父类
        // strategy.setSuperServiceClass("com.baomidou.demo.TestService");
        // 自定义 service 实现类父类
        // strategy.setSuperServiceImplClass("com.baomidou.demo.TestServiceImpl");
        // 自定义 mapper 父类
        // strategy.setSuperMapperClass("com.baomidou.demo.TestMapper");
        // 自定义 controller 父类
        // strategy.setSuperControllerClass("com.baomidou.demo.TestController");
        // 【实体】是否生成字段常量（默认 false）
        // public static final String ID = "test_id";
        // strategy.setEntityColumnConstant(true);
        // 【实体】是否为构建者模型（默认 false）
        // public User setName(String name) {this.name = name; return this;}
        // strategy.setEntityBuilderModel(true);
        strategy.setControllerMappingHyphenStyle(true);
        strategy.setEntityLombokModel(true);
        strategy.setEntitySerialVersionUID(true);
        strategy.setEntityTableFieldAnnotationEnable(true);
        mpg.setStrategy(strategy);
        // 4关闭默认 xml 生成，调整生成 至 根目录
        TemplateConfig templateConfig = new TemplateConfig();
        templateConfig.setXml(null);
        mpg.setTemplate(templateConfig);

        //5包配置
        PackageConfig pc = new PackageConfig();
        // 配置父包名
        pc.setParent("com.example.admin");
        // 配置模块名
//        pc.setModuleName("admin");
        // 配置 controller 包名
        pc.setController("controller");
        // 配置 service 包名
        pc.setService("service");
        // 配置 serviceImpl 包名
        pc.setServiceImpl("service.impl");
        // 配置 mapper 包名
        pc.setMapper("mapper");
        // 配置 entity 包名
        pc.setEntity("entity");
        mpg.setPackageInfo(pc);

        // 注入自定义配置，可以在 VM 中使用 cfg.abc 【可无】
        /*InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("abc", this.getConfig().getGlobalConfig().getAuthor() +
                        "-mp");
                this.setMap(map);
            }
        };*/

        // 自定义 xxList.jsp 生成
        /*List<FileOutConfig> focList = new ArrayList<>();

         focList.add(new FileOutConfig("/template/list.jsp.vm") {
             @Override
             public String outputFile(TableInfo tableInfo) {
                // 自定义输入文件名称
                 return "D://workspace/study/springboot_mybatisplus_lombok/src/main/webapp/" + tableInfo.getEntityName() + ".jsp";
             }
         });
         cfg.setFileOutConfigList(focList);
         mpg.setCfg(cfg);*/

        // 调整 xml 生成目录演示
        /*focList.add(new FileOutConfig("/templates/mapper.xml.ftl") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return "D://workspace/study/springboot_mybatisplus_lombok/src/main/resources/com/springboot/study/mapper/" + tableInfo.getEntityName()+"Mapper" + ".xml";
            }
        });
        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);*/

        // 自定义模板配置，可以 copy 源码 mybatis-plus/src/main/resources/templates 下面内容修改，
        // 放置自己项目的 src/main/resources/templates 目录下, 默认名称一下可以不配置，也可以自定义模板名称
        // TemplateConfig tc = new TemplateConfig();
        // tc.setController("...");
        // tc.setEntity("...");
        // tc.setMapper("...");
        // tc.setXml("...");
        // tc.setService("...");
        // tc.setServiceImpl("...");
        // 如上任何一个模块如果设置 空 OR Null 将不生成该模块。
        // mpg.setTemplate(tc);

        //执行生成
        mpg.execute();

        // 打印注入设置【可无】
        // System.err.println(mpg.getCfg().getMap().get("abc"));
    }

}

