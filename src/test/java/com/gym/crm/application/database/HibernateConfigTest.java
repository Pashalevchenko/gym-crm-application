package com.gym.crm.application.database;

import com.gym.crm.application.config.GymAppConfig;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = GymAppConfig.class)
public class HibernateConfigTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private HibernateTransactionManager transactionManager;

    @Test
    void context_ShouldLoadHibernateBeans() {
        assertNotNull(dataSource);
        assertNotNull(sessionFactory);
        assertNotNull(transactionManager);
    }
}
