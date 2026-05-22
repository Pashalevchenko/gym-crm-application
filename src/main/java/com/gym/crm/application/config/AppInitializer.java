package com.gym.crm.application.config;

import com.gym.crm.application.logging.RestLoggingFilter;
import com.gym.crm.application.logging.TransactionIdFilter;
import jakarta.servlet.Filter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{
                GymAppConfig.class,
                HibernateConfig.class
        };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{
                WebConfig.class
        };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    protected Filter[] getServletFilters() {
        return new Filter[]{
                new TransactionIdFilter(),
                new RestLoggingFilter()
        };
    }
}