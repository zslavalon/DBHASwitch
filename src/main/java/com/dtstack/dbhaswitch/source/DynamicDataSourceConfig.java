package com.dtstack.dbhaswitch.source;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class DynamicDataSourceConfig {

    @Bean
    @ConfigurationProperties("rdslite.datasource")
    public DataSource rdsDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

//    @Bean
//    @ConfigurationProperties("rdsdata.datasource")
//    public DataSource rdsdataDataSource(){
//        return DruidDataSourceBuilder.create().build();
//    }

    @Bean("rdsliteSqlSessionFactory")
    public SqlSessionFactory rdsliteSqlSessionFactory(@Qualifier("rdsDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);

        //分页插件
        Interceptor interceptor = new PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("helperDialect", "mysql");
        properties.setProperty("offsetAsPageNum", "true");
        properties.setProperty("rowBoundsWithCount", "true");
        properties.setProperty("reasonable", "true");
        properties.setProperty("supportMethodsArguments", "true");
        properties.setProperty("params", "pageNum=pageNumKey;pageSize=pageSizeKey;");
        interceptor.setProperties(properties);
        bean.setPlugins(new Interceptor[]{interceptor});
        //添加XML目录
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        return getSqlSessionFactory(bean, resolver);
    }

    @Bean("rdsliteSqlSessionTemplate")
    public SqlSessionTemplate rdsliteSqlSessionTemplate(@Qualifier("rdsliteSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        // 使用上面配置的Factory
        SqlSessionTemplate template = new SqlSessionTemplate(sqlSessionFactory);
        return template;
    }

//    @Bean(name = "rdsdataSqlSessionFactory")
//    public SqlSessionFactory rdsdataSqlSessionFactory(@Qualifier("rdsdataDataSource") DataSource dataSource) throws Exception {
//        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
//        bean.setDataSource(dataSource);
//        //添加XML目录
//        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//        return getSqlSessionFactory(bean, resolver);
//    }
//
//    @Bean(name = "rdsdataSqlSessionTemplate")
//    public SqlSessionTemplate rdsdataSqlSessionTemplate(@Qualifier("rdsdataSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
//        // 使用上面配置的Factory
//        SqlSessionTemplate template = new SqlSessionTemplate(sqlSessionFactory);
//        return template;
//    }


    private SqlSessionFactory getSqlSessionFactory(SqlSessionFactoryBean bean, ResourcePatternResolver resolver) {
        try {
            org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
            configuration.setMapUnderscoreToCamelCase(true);
            configuration.setUseColumnLabel(true);
            bean.setMapperLocations(resolver.getResources("classpath*:mapper/**/*.xml"));
            bean.setConfiguration(configuration);
            return bean.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Bean
    @Primary
    public DynamicDataSource dataSource(DataSource rdsDataSource, DataSource rdsDataDataSource, DataSource collectionDataSource) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceNames.RDSLITE, rdsDataSource);
//        targetDataSources.put(DataSourceNames.RDSDATA, rdsDataDataSource);
        return new DynamicDataSource(rdsDataSource, targetDataSources);
    }
}