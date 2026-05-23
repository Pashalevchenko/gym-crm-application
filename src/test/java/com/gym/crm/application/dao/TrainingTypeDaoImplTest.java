package com.gym.crm.application.dao;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.gym.crm.application.entity.TrainingType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Training type DAO DBUnit integration tests")
class TrainingTypeDaoImplTest extends AbstractDaoTest<TrainingTypeDao> {

    @Nested
    @DatabaseSetup(value = "/dataset/trainee-data-init.xml", type = DatabaseOperation.CLEAN_INSERT)
    @DisplayName("findAll")
    class FindAllTests {

        @Test
        @DisplayName("Should return all training types")
        void findAll_success() {
            List<TrainingType> actual = dao.findAll();

            assertThat(actual).hasSize(2);
            assertThat(actual)
                    .extracting(TrainingType::getTrainingTypeName)
                    .containsExactlyInAnyOrder("Penguin Yoga", "Strength Shenanigans");
        }
    }

    @Nested
    @DatabaseSetup(value = "/dataset/trainee-data-init.xml", type = DatabaseOperation.CLEAN_INSERT)
    @DisplayName("findByName")
    class FindByNameTests {

        @Test
        @DisplayName("Should return training type when name exists")
        void findByName_whenNameExists_shouldReturnTrainingType() {
            Optional<TrainingType> actual = dao.findByName("Penguin Yoga");

            assertThat(actual).isPresent();
            assertThat(actual.get().getTrainingTypeName()).isEqualTo("Penguin Yoga");
        }

        @Test
        @DisplayName("Should return empty optional when name does not exist")
        void findByName_whenNameDoesNotExist_shouldReturnEmptyOptional() {
            Optional<TrainingType> actual = dao.findByName("Boxing");

            assertThat(actual).isEmpty();
        }
    }
}