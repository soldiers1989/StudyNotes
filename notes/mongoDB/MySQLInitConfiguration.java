/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lc.config;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

/**
 *
 * @author Liu Jianwei
 * @date Aug 23, 2016 3:56:39 PM
 */
@Configuration
@MapperScan("com.lc.mapper")
public class MySQLInitConfiguration {

    public MySQLInitConfiguration() {
       System.out.println("LCLCLC mybatis start...");
    }

//    @Bean(name = "mapperScannerConfigurer")
//    public MapperScannerConfigurer mapperScannerConfigurer() throws Exception {
//        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
////        mapperScannerConfigurer.setMarkerInterface(BaseMapper.class);
//        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactoryBean");
//        mapperScannerConfigurer.setBasePackage("com.vcredit.**.mapper");
//        return mapperScannerConfigurer;
//    }
    @Bean(name = "dataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        PoolProperties p = new PoolProperties();
        p.setJmxEnabled(true);
        p.setTestWhileIdle(true);
        p.setTestOnBorrow(true);
        p.setValidationQuery("SELECT 1");
        p.setTestOnReturn(false);
        p.setValidationInterval(30000);
        p.setTimeBetweenEvictionRunsMillis(30000);
        p.setMaxActive(100);
        p.setInitialSize(10);
        p.setMaxWait(10000);
        p.setRemoveAbandonedTimeout(180);
        p.setMinEvictableIdleTimeMillis(30000);
        p.setMinIdle(10);
        p.setLogAbandoned(true);
//        p.setRemoveAbandoned(true);
        p.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
                + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
        DataSource dataSource = new DataSource();
        dataSource.setPoolProperties(p);
        return dataSource;
    }

    @Bean(name = "sqlSessionFactoryBean")
    public SqlSessionFactory sqlSessionFactoryBean(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        /** 设置mybatis configuration 扫描路径 */
//        sqlSessionFactoryBean.setConfigLocation(new ClassPathResource("mybatis-config.xml"));

        sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath:/mybatis/*.xml"));
//        sqlSessionFactoryBean.setTypeAliasesPackage("com.vcredit.o2o.app.model.dto;com.vcredit.o2o.app.model.mongo;com.vcredit.o2o.app.model.service.customer");

        return sqlSessionFactoryBean.getObject();
    }

    @Bean(name = "sqlSession")
    public SqlSession sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        SqlSession sqlSession = new SqlSessionTemplate(sqlSessionFactory);
        return sqlSession;
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
