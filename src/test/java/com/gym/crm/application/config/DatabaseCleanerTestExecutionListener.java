package com.gym.crm.application.config;

import org.springframework.core.Ordered;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

public class DatabaseCleanerTestExecutionListener extends AbstractTestExecutionListener {

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        DataSource dataSource = testContext
                .getApplicationContext()
                .getBean(DataSource.class);

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

//            statement.execute("SET REFERENTIAL_INTEGRITY FALSE");
//            statement.execute("TRUNCATE TABLE trainee_trainer RESTART IDENTITY");
//            statement.execute("TRUNCATE TABLE trainings RESTART IDENTITY");
//            statement.execute("TRUNCATE TABLE trainees RESTART IDENTITY");
//            statement.execute("TRUNCATE TABLE trainers RESTART IDENTITY");
//            statement.execute("TRUNCATE TABLE training_types RESTART IDENTITY");
//            statement.execute("TRUNCATE TABLE users RESTART IDENTITY");
//            statement.execute("SET REFERENTIAL_INTEGRITY TRUE");
        }
    }
}