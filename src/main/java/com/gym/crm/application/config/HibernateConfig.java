package com.gym.crm.application.config;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class HibernateConfig {

    @Value("${db.driver}")
    private String driver;

    @Value("${db.url}")
    private String url;

    @Value("${db.username}")
    private String username;

    @Value("${db.password}")
    private String password;

    @Bean
    public SessionFactory sessionFactory() {
        Properties props = new Properties();
        props.put("hibernate.connection.driver_class", driver);
        props.put("hibernate.connection.url", url);
        props.put("hibernate.connection.username", username);
        props.put("hibernate.connection.password", password);
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.put("hibernate.show_sql", "true");
        props.put("hibernate.format_sql", "true");
        props.put("hibernate.hbm2ddl.auto", "validate");

        return new org.hibernate.cfg.Configuration()
                .setProperties(props)
                .buildSessionFactory();
    }
}
