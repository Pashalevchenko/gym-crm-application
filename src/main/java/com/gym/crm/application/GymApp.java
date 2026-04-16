package com.gym.crm.application;

import com.gym.crm.application.config.StorageConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Map;

public class GymApp {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(GymApp.class);

        AnnotationConfigApplicationContext contextStorage =
                new AnnotationConfigApplicationContext(StorageConfig.class);

        Map<String, Map<Long, ?>> storage = (Map<String, Map<Long, ?>>) contextStorage.getBean("mainStorage");

        storage.forEach((namespace, dataMap) -> {
            System.out.println(namespace);

            dataMap.forEach((id, entity) -> {
                System.out.println("  Id: " + id + " -> " + entity);
            });
        });
    }
}
