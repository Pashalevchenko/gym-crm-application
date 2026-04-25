package com.gym.crm.application.database;

import com.gym.crm.application.config.GymAppConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = GymAppConfig.class)
public class HibernateConfigTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void context_ShouldCreateSessionFactoryBean() {
        assertTrue(context.containsBean("sessionFactory"));
    }
}
