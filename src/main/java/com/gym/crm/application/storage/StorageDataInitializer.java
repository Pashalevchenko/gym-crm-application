package com.gym.crm.application.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.crm.application.model.Trainee;
import com.gym.crm.application.model.Trainer;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

@Component
public class StorageDataInitializer implements BeanPostProcessor {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Value("${storage.data.path}")
    private String dataFilePath;

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

           fillNamespaceStore("Trainee", data, storage,
                   new TypeReference<>() {}, Trainee::getId);

           fillNamespaceStore("Trainer", data, storage,
                   new TypeReference<>() {}, Trainer::getId);

           fillNamespaceStore("Training", data, storage,
                   new TypeReference<>() {}, createIdGenerator());

        } catch (Exception e) {
            throw new BeanInitializationException(e.getMessage());
        }
    }

    private <T> void fillNamespaceStore (
            String key,
            Map<String, List<Object>> data,
            Map<String, Map<Long, ?>> storage,
            TypeReference<List<T>> typeRef,
            Function<T, Long> idExtractor
    ) {
        if (data.containsKey(key)) {
            Map<Long, T> targetMap = (Map<Long, T>) storage.get(key);
            List<T> list = objectMapper.convertValue(data.get(key), typeRef);
            list.forEach(item -> targetMap.put(idExtractor.apply(item), item));
        }
    }

    private <T> Function<T, Long> createIdGenerator() {
        AtomicLong counter = new AtomicLong(1);
        return t -> counter.getAndIncrement();
    }
}
