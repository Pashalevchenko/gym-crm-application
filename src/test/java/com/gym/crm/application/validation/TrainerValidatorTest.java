package com.gym.crm.application.validation;

import com.gym.crm.application.entity.Trainer;
import com.gym.crm.application.entity.TrainingType;
import com.gym.crm.application.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TrainerValidatorTest {

    private TrainerValidator trainerValidator;

    @BeforeEach
    void setUp() {
        trainerValidator = new TrainerValidator();
    }

    @Test
    @DisplayName("Should pass validation when trainer create request is valid")
    void validateForCreate_whenTrainerIsValid_shouldNotThrowException() {
        Trainer trainer = Trainer.builder()
                .user(User.builder()
                        .firstName("Ivan")
                        .lastName("Ivanov")
                        .build())
                .specialization(TrainingType.builder()
                        .trainingTypeName("Yoga")
                        .build())
                .build();

        assertDoesNotThrow(() -> trainerValidator.validateForCreate(trainer));
    }

    @Test
    @DisplayName("Should throw exception when trainer create request is null")
    void validateForCreate_whenTrainerIsNull_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> trainerValidator.validateForCreate(null));
    }

    @Test
    @DisplayName("Should throw exception when trainer first name is blank")
    void validateForCreate_whenFirstNameIsBlank_shouldThrowException() {
        Trainer trainer = Trainer.builder()
                .user(User.builder()
                        .firstName(" ")
                        .lastName("Ivanov")
                        .build())
                .build();

        assertThrows(IllegalArgumentException.class, () -> trainerValidator.validateForCreate(trainer));
    }

    @Test
    @DisplayName("Should throw exception when trainer last name is blank")
    void validateForCreate_whenLastNameIsBlank_shouldThrowException() {
        Trainer trainer = Trainer.builder()
                .user(User.builder()
                        .firstName("Ivan")
                        .lastName(" ")
                        .build())
                .build();

        assertThrows(IllegalArgumentException.class, () -> trainerValidator.validateForCreate(trainer));
    }
}
