package com.gym.crm.application.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.crm.application.model.Trainee;
import com.gym.crm.application.model.Trainer;
import com.gym.crm.application.model.Training;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Component
public class StorageDataInitializer implements BeanPostProcessor {

    @Value("${storage.data.path}")
    private String dataFilePath;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if ("mainStorage".equals(beanName)) {
            Map<String, Map<Long, ?>> storage = (Map<String, Map<Long, ?>>) bean;
            loadJsonToStorage(storage);
        }
        return bean;
    }

    private void loadJsonToStorage(Map<String, Map<Long, ?>> storage) {
        try {
            ClassPathResource resource = new ClassPathResource(dataFilePath);
            Map<String, List<Object>> data = objectMapper.readValue(
                    resource.getInputStream(), new TypeReference<>() {}
            );

            if (data.containsKey("Trainee")) {
                Map<Long, Trainee> traineeMap = (Map<Long, Trainee>) storage.get("Trainee");
                List<Trainee> list = objectMapper.convertValue(data.get("Trainee"), new TypeReference<>() {});
                list.forEach(t -> traineeMap.put(t.getId(), t));
            }

            if (data.containsKey("Trainer")) {
                Map<Long, Trainer> trainerMap = (Map<Long, Trainer>) storage.get("Trainer");
                List<Trainer> list = objectMapper.convertValue(data.get("Trainer"), new TypeReference<>() {});
                list.forEach(t -> trainerMap.put(t.getId(), t));
            }

            if (data.containsKey("Training")) {
                Map<Long, Training> trainingMap = (Map<Long, Training>) storage.get("Training");
                List<Training> list = objectMapper.convertValue(data.get("Training"), new TypeReference<>() {});

                for (int i = 0; i < list.size(); i++) {
                    trainingMap.put((long) i + 1, list.get(i));
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
