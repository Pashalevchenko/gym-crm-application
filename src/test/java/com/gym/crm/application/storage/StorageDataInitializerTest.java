package com.gym.crm.application.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StorageDataInitializerTest {

    private StorageDataInitializer initializer;
    private Map<String, Map<Long, Object>> mockStorage;

    @BeforeEach
    void setUp() {
        initializer = new StorageDataInitializer();
        ReflectionTestUtils.setField(initializer, "dataFilePath", "test-storage.json");

        mockStorage = new HashMap<>();
        mockStorage.put("Trainee", new HashMap<>());
        mockStorage.put("Trainer", new HashMap<>());
        mockStorage.put("Training", new HashMap<>());
    }

    @Test
    @DisplayName("Should load data when bean name is mainStorage")
    void postProcessAfterInitialization_WithCorrectBeanName() {
        Object result = initializer.postProcessAfterInitialization(mockStorage, "mainStorage");

        assertNotNull(result);

        assertFalse(mockStorage.get("Trainee").isEmpty(), "Trainee storage should not be empty");
        assertFalse(mockStorage.get("Trainer").isEmpty(), "Trainer storage should not be empty");
        assertFalse(mockStorage.get("Training").isEmpty(), "Training storage should not be empty");
    }

    @Test
    @DisplayName("Should bypass bean processing when bean name does not match the target storage component")
    void postProcessAfterInitialization_WithOtherBeanName() {
        String expected = "someOtherBean";

        Object actual = initializer.postProcessAfterInitialization(expected, "notMainStorage");

        assertEquals(expected, actual);
        assertTrue(mockStorage.get("Trainee").isEmpty());
    }

    @Test
    @DisplayName("Should throw exception when file path is invalid")
    void loadJsonToStorage_ThrowsExceptionOnInvalidPath() {
        ReflectionTestUtils.setField(initializer, "dataFilePath", "non-existent-file.json");

        assertThrows(BeanInitializationException.class, () ->
                initializer.postProcessAfterInitialization(mockStorage, "mainStorage"));
    }
}