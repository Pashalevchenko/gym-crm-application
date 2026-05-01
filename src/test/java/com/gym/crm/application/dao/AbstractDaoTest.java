package com.gym.crm.application.dao;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.gym.crm.application.config.DaoTestConfig;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DaoTestConfig.class)
@TestExecutionListeners(listeners = {
                DependencyInjectionTestExecutionListener.class,
                DbUnitTestExecutionListener.class
        },
                      mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public abstract class AbstractDaoTest<T> {

    @Autowired
    protected T dao;

    @Autowired
    protected SessionFactory sessionFactory;

}