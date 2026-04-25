package com.gym.crm.application.database;

import com.gym.crm.application.config.GymAppConfig;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = GymAppConfig.class)
public class HibernateConfigTest {

    @Autowired
    private SessionFactory sessionFactory;

    @Test
    void sessionFactory_whenContextLoaded_isNotNull() {
        assertNotNull(sessionFactory);
    }

    @Test
    void sessionFactory_whenCreated_isNotClosed() {
        assertFalse(sessionFactory.isClosed());
    }

    @Test
    void session_whenOpened_isOpen() {
        try (Session session = sessionFactory.openSession()) {
            assertTrue(session.isOpen());
        }
    }

    @Test
    void session_whenOpened_isConnectedToDatabase() {
        try (Session session = sessionFactory.openSession()) {
            assertTrue(session.isConnected());
        }
    }
}
