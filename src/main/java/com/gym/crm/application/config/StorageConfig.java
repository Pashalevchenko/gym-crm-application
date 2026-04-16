package com.gym.crm.application.config;

import com.gym.crm.application.model.Trainee;
import com.gym.crm.application.model.Trainer;
import com.gym.crm.application.model.Training;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ComponentScan("com.gym.crm.application")
public class StorageConfig {

    @Bean
    public Map<Long, Trainee> traineeStorage(){
        return new HashMap<>();
    }

    @Bean
    public Map<Long, Trainer> trainerStorage(){
        return new HashMap<>();
    }

    @Bean
    public Map<Long, Training> trainingStorage(){
        return new HashMap<>();
    }

    @Bean()
    public Map<String, Map<Long, ?>> mainStorage(
            Map<Long, Trainee> traineeStorage,
            Map<Long, Trainer> trainerStorage,
            Map<Long, Training> trainingStorage
    ){
        Map<String, Map<Long, ?>> main = new HashMap<>();

        main.put("Trainee", traineeStorage);
        main.put("Trainer", trainerStorage);
        main.put("Training", trainingStorage);
        
        return main;
    }
}
