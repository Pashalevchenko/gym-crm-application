package com.gym.crm.application;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class GymApp {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(GymApp.class);

    }
}
