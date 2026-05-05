package com.gym.crm.application.dao;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.gym.crm.application.entity.Trainee;
import com.gym.crm.application.entity.Trainer;
import com.gym.crm.application.entity.Training;
import com.gym.crm.application.entity.TrainingType;
import org.hibernate.Session;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Training DAO DBUnit integration tests")
class TrainingDaoImplTest extends AbstractDaoTest<TrainingDao> {

    private static final Long TRAINING_ID = 10L;
    private static final Long SECOND_TRAINING_ID = 12L;
    private static final Long TRAINEE_ID = 10L;
    private static final Long TRAINER_ID = 10L;
    private static final Long TRAINING_TYPE_ID = 10L;

    @Nested
    @DatabaseSetup(value = "/dataset/training-data-init.xml", type = DatabaseOperation.CLEAN_INSERT)
    @DisplayName("create")
    class CreateTests {

        @Test
        @DisplayName("Should save training")
        void create_success() {
            Training training = buildTraining("New Training", LocalDate.of(2026, 5, 1), 50);
            Training actual = dao.create(training);

            assertThat(actual).isNotNull();
            assertThat(actual.getId()).isNotNull();

            Optional<Training> found = dao.findById(actual.getId());

            assertThat(found).isPresent();

            Training saved = found.get();

            assertThat(saved.getTrainingName()).isEqualTo("New Training");
            assertThat(saved.getTrainingDate()).isEqualTo(LocalDate.of(2026, 5, 1));
            assertThat(saved.getTrainingDuration()).isEqualTo(50);
            assertThat(saved.getTrainingType().getId()).isEqualTo(TRAINING_TYPE_ID);
            assertThat(saved.getTrainee().getId()).isEqualTo(TRAINEE_ID);
            assertThat(saved.getTrainer().getId()).isEqualTo(TRAINER_ID);
        }

        @Test
        @DisplayName("Should throw exception when training is null")
        void create_nullTraining() {
            RuntimeException exception = assertThrows(RuntimeException.class, () -> dao.create(null));

            assertThat(exception).isNotNull();
        }
    }

    @Nested
    @DatabaseSetup(value = "/dataset/training-data-init.xml", type = DatabaseOperation.CLEAN_INSERT)
    @DisplayName("findById")
    class FindByIdTests {

        @Test
        @DisplayName("Should return training when training with requested id exists")
        void findById_found() {
            Optional<Training> found = dao.findById(TRAINING_ID);

            assertThat(found).isPresent();

            Training actual = found.get();

            assertThat(actual.getId()).isEqualTo(TRAINING_ID);
            assertThat(actual.getTrainingName()).isEqualTo("Morning Penguin Stretch");
            assertThat(actual.getTrainingDate()).isEqualTo(LocalDate.of(2026, 4, 10));
            assertThat(actual.getTrainingDuration()).isEqualTo(60);
            assertThat(actual.getTrainingType().getId()).isEqualTo(10L);
            assertThat(actual.getTrainingType().getTrainingTypeName()).isEqualTo("Penguin Yoga");
            assertThat(actual.getTrainee().getId()).isEqualTo(10L);
            assertThat(actual.getTrainee().getUser().getUsername()).isEqualTo("borys.burpee");
            assertThat(actual.getTrainer().getId()).isEqualTo(10L);
            assertThat(actual.getTrainer().getUser().getUsername()).isEqualTo("pavlo.plank");
        }

        @Test
        @DisplayName("Should return empty optional when training with requested id does not exist")
        void findById_notFound() {
            Optional<Training> actual = dao.findById(999L);

            assertThat(actual).isEmpty();
        }
    }

    @Nested
    @DatabaseSetup(value = "/dataset/training-data-init.xml", type = DatabaseOperation.CLEAN_INSERT)
    @DisplayName("findAll")
    class FindAllTests {

        @Test
        @DisplayName("Should return all trainings")
        void findAll_success() {
            List<Training> actual = dao.findAll();

            assertThat(actual).hasSize(2);
            assertThat(actual)
                    .extracting(Training::getId)
                    .containsExactlyInAnyOrder(TRAINING_ID, SECOND_TRAINING_ID);
            assertThat(actual)
                    .extracting(Training::getTrainingName)
                    .containsExactlyInAnyOrder("Morning Penguin Stretch", "Evening Strength");
        }

        @Test
        @DisplayName("Should return empty list when there are no trainings")
        void findAll_empty() {
            deleteTraining(SECOND_TRAINING_ID);
            deleteTraining(TRAINING_ID);

            List<Training> actual = dao.findAll();

            assertThat(actual).isEmpty();
        }
    }

    private Training buildTraining(String trainingName, LocalDate trainingDate, Integer duration) {
        Trainee trainee = findTraineeById(TRAINEE_ID);
        Trainer trainer = findTrainerById(TRAINER_ID);
        TrainingType trainingType = findTrainingTypeById(TRAINING_TYPE_ID);

        return Training.builder()
                .trainingName(trainingName)
                .trainingDate(trainingDate)
                .trainingDuration(duration)
                .trainee(trainee)
                .trainer(trainer)
                .trainingType(trainingType)
                .build();
    }

    private Trainee findTraineeById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Trainee.class, id);
        }
    }

    private Trainer findTrainerById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Trainer.class, id);
        }
    }

    private TrainingType findTrainingTypeById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(TrainingType.class, id);
        }
    }

    private void deleteTraining(Long id) {
        try (Session session = sessionFactory.openSession()) {
            var transaction = session.beginTransaction();
            Training training = session.get(Training.class, id);

            if (training != null) {
                session.remove(training);
            }

            transaction.commit();
        }
    }
}