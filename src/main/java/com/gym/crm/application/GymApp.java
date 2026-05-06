package com.gym.crm.application;

import com.gym.crm.application.facade.GymAppFacade;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.gym.crm.application")
public class GymApp {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(GymApp.class);

    }
}
