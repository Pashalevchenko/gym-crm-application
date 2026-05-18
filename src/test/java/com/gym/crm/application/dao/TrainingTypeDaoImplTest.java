package com.gym.crm.application.dao;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.gym.crm.application.entity.TrainingType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.util.List;
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
}