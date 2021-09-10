package com.example.generator.config;

import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.extension.parsers.BlockAttackSqlParser;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.optimize.JsqlParserCountOptimize;
import io.swagger.annotations.ApiOperation;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
@MapperScan("com.example.**.mapper")
public class MybatisPlusConfig {

    @Bean
    @ApiOperation("分页插件")
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        //防止恶意sql注入
        ArrayList<ISqlParser> sqlParsers = new ArrayList<>();
        sqlParsers.add(new BlockAttackSqlParser());
        paginationInterceptor.setSqlParserList(sqlParsers);
        paginationInterceptor.setCountSqlParser(new JsqlParserCountOptimize(true));
        return paginationInterceptor;
    }

    /*@Bean
    @ApiOperation("乐观锁插件")
    public OptimisticLockerInterceptor optimisticLockerInterceptor() {
        return new OptimisticLockerInterceptor();
    }

    //在mysql中，主键往往是自增长的，这样使用起来是比较方便的，如果使用的是Oracle数据库，那么就不能使用自增长了，就得使用Sequence 序列生成id值
    @Bean
    @ApiOperation("序列生成器")
    public OracleKeyGenerator oracleKeyGenerator() {
        return new OracleKeyGenerator();
    }

    @Bean
    @ApiOperation("逻辑删除")
    public ISqlInjector sqlInjector() {
        return new LogicSqlInjector();
    }

    @Bean
    @ApiOperation("设置属性")
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setDefaultScriptingLanguage(MybatisXMLLanguageDriver.class);
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        configuration.setMapUnderscoreToCamelCase(true); //驼峰标识
        configuration.setCacheEnabled(false);
        configuration.setCallSettersOnNulls( true );
        sqlSessionFactory.setConfiguration(configuration);
        sqlSessionFactory.setPlugins(new Interceptor[]{
//    		performanceInterceptor(), //性能分析
                optimisticLockerInterceptor(),//乐观锁
                paginationInterceptor() //添加分页功能

        });
        sqlSessionFactory.setGlobalConfig(globalConfiguration());
        sqlSessionFactory.setMapperLocations(resolver.getResources("classpath:/mapper/*.xml")); // Mapper包路径
        return sqlSessionFactory.getObject();


    }

    @Bean
    @ApiOperation("设置策略")
    public GlobalConfig globalConfiguration() {
        GlobalConfig conf = new GlobalConfig();
        DbConfig dbconf = new DbConfig();
        dbconf.setIdType(IdType.ID_WORKER_STR);
        dbconf.setLogicDeleteValue("D");
        dbconf.setLogicNotDeleteValue("A");
        dbconf.setDbType(DbType.MYSQL);
        dbconf.setFieldStrategy(FieldStrategy.NOT_EMPTY);
        conf.setDbConfig(dbconf);
        conf.setMetaObjectHandler(new MetaObjectHandlerConfig());
        conf.setSqlInjector(sqlInjector());
        return conf;
    }*/
}
