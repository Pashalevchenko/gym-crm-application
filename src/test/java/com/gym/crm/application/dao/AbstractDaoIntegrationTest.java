package com.gym.crm.application.dao;

import com.gym.crm.application.config.DaoTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DaoTestConfig.class)
public abstract class AbstractDaoIntegrationTest {

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void cleanDatabase() throws Exception {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute("""
                    TRUNCATE TABLE
                        trainee_trainer,
                        trainings,
                        trainees,
                        trainers,
                        training_types,
                        users
                    RESTART IDENTITY CASCADE
                    """);
        }
    }
}