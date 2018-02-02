package com.ddk.en.db;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Created by Evan on 2017-07-07.
 */
@Configuration
@MapperScan(basePackages = "com.ddk.en.db.mapper.ddko2o", sqlSessionTemplateRef = "sqlSession")
@Import({PlatformDBConfig.class, DDKOnlineDBConfig.class})
public class DDKO2oDBConfig {

    private static final Logger logger = Logger.getLogger(DDKO2oDBConfig.class);

    public DDKO2oDBConfig() {
        logger.info("Load and init ddko2o mysql server....");
    }


    @Bean(name = "dataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.ddko2o")
    public DataSource dataSource() {
        PoolProperties p = new PoolProperties();
        p.setJmxEnabled(true);
        p.setTestWhileIdle(true);
        p.setTestOnBorrow(true);
        p.setValidationQuery("SELECT 1");
        p.setTestOnReturn(false);
        p.setValidationInterval(30000);
        p.setTimeBetweenEvictionRunsMillis(30000);
        p.setMaxActive(200);
        p.setInitialSize(20);
        p.setMaxWait(10000);
        p.setRemoveAbandonedTimeout(180);
        p.setMinEvictableIdleTimeMillis(30000);
        p.setMinIdle(10);
        p.setLogAbandoned(true);
        p.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;" +
                "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
        DataSource dataSource = new DataSource();
        dataSource.setPoolProperties(p);
        return dataSource;
    }

    @Bean(name = "sqlSessionFactoryBean")
    @Primary
    public SqlSessionFactory sqlSessionFactoryBean(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath:/ddko2o/*.xml"));
        logger.info("<========== Mysql ddko2o DataSource ==========>: " + dataSource.getUrl());
        return sqlSessionFactoryBean.getObject();
    }

    @Bean(name = "sqlSession")
    @Primary
    public SqlSession sqlSessionTemplate(SqlSessionFactory sqlSessionFactoryBean) {
        SqlSession sqlSession = new SqlSessionTemplate(sqlSessionFactoryBean);
        return sqlSession;
    }

    @Bean(name = "transactionManager")
    @Primary
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

}

