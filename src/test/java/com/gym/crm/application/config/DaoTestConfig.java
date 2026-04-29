package com.gym.crm.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Import({
        DatabaseConfig.class,
        LiquibaseConfig.class,
        HibernateConfig.class
})
@ComponentScan("com.gym.crm.application.dao.impl")
@PropertySource("classpath:application-test.properties")
public class DaoTestConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}