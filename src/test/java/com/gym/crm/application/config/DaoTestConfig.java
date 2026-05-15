package com.gym.crm.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;

@Import({
        DatabaseConfig.class,
        LiquibaseConfig.class,
        HibernateConfig.class,
        DbUnitConfig.class
})
@ComponentScan({"com.gym.crm.application.dao.impl", "com.gym.crm.application.search"})
@ComponentScan(basePackageClasses = TransactionHandler.class,
               useDefaultFilters = false,
               includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
               classes = TransactionHandler.class))
@PropertySource("classpath:application-test.properties")
public class DaoTestConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}