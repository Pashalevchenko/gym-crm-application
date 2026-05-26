package com.gym.crm.application.repository;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;
import com.gym.crm.application.config.DbUnitConfig;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;

import javax.sql.DataSource;

@DataJpaTest
@ActiveProfiles("test")
@Import(DbUnitConfig.class)
@TestExecutionListeners(
        value = DbUnitTestExecutionListener.class,
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
@DbUnitConfiguration(databaseConnection = "dbUnitDatabaseConnection")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public abstract class AbstractRepositoryTest<T> {

    @Autowired
    protected T repository;

    @TestConfiguration
    static class DbUnitConfig {

        @Bean
        public DatabaseConfigBean dbUnitDatabaseConfig() {
            DatabaseConfigBean config = new DatabaseConfigBean();
            config.setDatatypeFactory(new H2DataTypeFactory());
            config.setQualifiedTableNames(false);

            return config;
        }

        @Bean(name = "dbUnitDatabaseConnection")
        public DatabaseDataSourceConnectionFactoryBean dbUnitDatabaseConnection(DataSource dataSource, DatabaseConfigBean dbUnitDatabaseConfig) {
            DatabaseDataSourceConnectionFactoryBean connectionFactory = new DatabaseDataSourceConnectionFactoryBean(dataSource);
            connectionFactory.setSchema("PUBLIC");
            connectionFactory.setDatabaseConfig(dbUnitDatabaseConfig);

            return connectionFactory;
        }
    }
}