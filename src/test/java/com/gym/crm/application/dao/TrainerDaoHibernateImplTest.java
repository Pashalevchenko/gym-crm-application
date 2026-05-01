package com.gym.crm.application.dao;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.gym.crm.application.entity.Trainer;
import com.gym.crm.application.entity.TrainingType;
import com.gym.crm.application.entity.User;
import org.hibernate.Session;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Trainer Hibernate DAO DBUnit integration tests")
class TrainerDaoHibernateImplTest extends AbstractDaoTest<TrainerDaoHibernate> {

    private static final Long TRAINER_ID = 10L;
    private static final Long SECOND_TRAINER_ID = 12L;
    private static final Long THIRD_TRAINER_ID = 15L;
    private static final Long TRAINER_USER_ID = 10L;
    private static final Long SECOND_TRAINER_USER_ID = 12L;
    private static final Long SPECIALIZATION_ID = 10L;
    private static final Long SECOND_SPECIALIZATION_ID = 12L;

    @Nested
    @DatabaseSetup(value = "/dataset/trainer-data-init.xml", type = DatabaseOperation.CLEAN_INSERT)
    @DisplayName("create")
    class CreateTests {

        @Test
        @DisplayName("Should save trainer")
        void create_success() {
            Trainer trainer = buildTrainer("New", "Trainer", "new.trainer", SPECIALIZATION_ID);
            Trainer actual = dao.create(trainer);

            assertThat(actual).isNotNull();
            assertThat(actual.getId()).isNotNull();
            assertThat(actual.getUser()).isNotNull();
            assertThat(actual.getUser().getId()).isNotNull();

            Optional<Trainer> found = dao.findByUsername("new.trainer");

            assertThat(found).isPresent();

            Trainer saved = found.get();
            assertThat(saved.getUser().getFirstName()).isEqualTo("New");
            assertThat(saved.getUser().getLastName()).isEqualTo("Trainer");
            assertThat(saved.getUser().getUsername()).isEqualTo("new.trainer");
            assertThat(saved.getUser().getPassword()).isEqualTo("12345");
            assertThat(saved.getUser().isActive()).isTrue();
            assertThat(saved.getSpecialization().getId()).isEqualTo(SPECIALIZATION_ID);
        }

        @Test
        @DisplayName("Should throw exception when trainer is null")
        void create_nullTrainer() {
            RuntimeException exception = assertThrows(RuntimeException.class, () -> dao.create(null));

            assertThat(exception).isNotNull();
        }
    }

    @Nested
    @DatabaseSetup(value = "/dataset/trainer-data-init.xml", type = DatabaseOperation.CLEAN_INSERT)
    @DisplayName("update")
    class UpdateTests {

        @Test
        @DisplayName("Should update trainer when trainer exists")
        void update_success() {
            Trainer existing = dao.findById(TRAINER_ID).orElseThrow();

            User user = User.builder()
                    .id(existing.getUser().getId())
                    .firstName("Updated")
                    .lastName("Trainer")
                    .username(existing.getUser().getUsername())
                    .password(existing.getUser().getPassword())
                    .isActive(existing.getUser().isActive())
                    .build();
            TrainingType specialization = TrainingType.builder()
                    .id(SECOND_SPECIALIZATION_ID)
                    .trainingTypeName("Strength Shenanigans")
                    .build();
            Trainer trainerToUpdate = Trainer.builder()
                    .id(existing.getId())
                    .user(user)
                    .specialization(specialization)
                    .build();

            Trainer updated = dao.update(trainerToUpdate);
            Optional<Trainer> found = dao.findById(updated.getId());

            assertThat(found).isPresent();

            Trainer actual = found.get();
            assertThat(actual.getId()).isEqualTo(TRAINER_ID);
            assertThat(actual.getUser().getId()).isEqualTo(TRAINER_USER_ID);
            assertThat(actual.getUser().getFirstName()).isEqualTo("Updated");
            assertThat(actual.getUser().getLastName()).isEqualTo("Trainer");
            assertThat(actual.getUser().getUsername()).isEqualTo("pavlo.plank");
            assertThat(actual.getUser().getPassword()).isEqualTo("12345");
            assertThat(actual.getUser().isActive()).isTrue();
            assertThat(actual.getSpecialization().getId()).isEqualTo(SECOND_SPECIALIZATION_ID);
            assertThat(actual.getSpecialization().getTrainingTypeName()).isEqualTo("Strength Shenanigans");
        }

        @Test
        @DisplayName("Should throw exception when trainer is null")
        void update_nullTrainer() {
            RuntimeException exception = assertThrows(RuntimeException.class, () -> dao.update(null));

            assertThat(exception).isNotNull();
        }
    }

    @Nested
    @DatabaseSetup(value = "/dataset/trainer-data-init.xml", type = DatabaseOperation.CLEAN_INSERT)
    @DisplayName("findById")
    class FindByIdTests {

        @Test
        @DisplayName("Should return trainer when trainer with requested id exists")
        void findById_found() {
            Optional<Trainer> found = dao.findById(TRAINER_ID);

            assertThat(found).isPresent();

            Trainer actual = found.get();
            assertThat(actual.getId()).isEqualTo(TRAINER_ID);
            assertThat(actual.getUser().getId()).isEqualTo(TRAINER_USER_ID);
            assertThat(actual.getUser().getFirstName()).isEqualTo("Pavlo");
            assertThat(actual.getUser().getLastName()).isEqualTo("Plank");
            assertThat(actual.getUser().getUsername()).isEqualTo("pavlo.plank");
            assertThat(actual.getUser().getPassword()).isEqualTo("12345");
            assertThat(actual.getUser().isActive()).isTrue();
            assertThat(actual.getSpecialization().getId()).isEqualTo(SPECIALIZATION_ID);
            assertThat(actual.getSpecialization().getTrainingTypeName()).isEqualTo("Penguin Yoga");
        }

        @Test
        @DisplayName("Should return empty optional when trainer with requested id does not exist")
        void findById_notFound() {
            Optional<Trainer> actual = dao.findById(999L);

            assertThat(actual).isEmpty();
        }
    }

    @Nested
    @DatabaseSetup(value = "/dataset/trainer-data-init.xml", type = DatabaseOperation.CLEAN_INSERT)
    @DisplayName("findByUsername")
    class FindByUsernameTests {

        @Test
        @DisplayName("Should return trainer when trainer with requested username exists")
        void findByUsername_found() {
            Optional<Trainer> found = dao.findByUsername("fedir.foamroller");

            assertThat(found).isPresent();

            Trainer actual = found.get();
            assertThat(actual.getId()).isEqualTo(SECOND_TRAINER_ID);
            assertThat(actual.getUser().getId()).isEqualTo(SECOND_TRAINER_USER_ID);
            assertThat(actual.getUser().getFirstName()).isEqualTo("Fedir");
            assertThat(actual.getUser().getLastName()).isEqualTo("Foamroller");
            assertThat(actual.getUser().getUsername()).isEqualTo("fedir.foamroller");
            assertThat(actual.getUser().getPassword()).isEqualTo("12345");
            assertThat(actual.getUser().isActive()).isTrue();
            assertThat(actual.getSpecialization().getId()).isEqualTo(SECOND_SPECIALIZATION_ID);
            assertThat(actual.getSpecialization().getTrainingTypeName()).isEqualTo("Strength Shenanigans");
        }

        @Test
        @DisplayName("Should return empty optional when trainer with requested username does not exist")
        void findByUsername_notFound() {
            Optional<Trainer> found = dao.findByUsername("ghost.trainer");

            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DatabaseSetup(value = "/dataset/trainer-data-init.xml", type = DatabaseOperation.CLEAN_INSERT)
    @DisplayName("findAll")
    class FindAllTests {

        @Test
        @DisplayName("Should return all trainers")
        void findAll_success() {
            List<Trainer> actual = dao.findAll();

            assertThat(actual).hasSize(3);

            assertThat(actual)
                    .extracting(trainer -> trainer.getUser().getUsername())
                    .containsExactlyInAnyOrder("pavlo.plank", "fedir.foamroller", "ira.iron");
        }

        @Test
        @DisplayName("Should return empty list when there are no trainers")
        void findAll_empty() {
            deleteTrainer(THIRD_TRAINER_ID);
            deleteTrainer(SECOND_TRAINER_ID);
            deleteTrainer(TRAINER_ID);

            List<Trainer> actual = dao.findAll();

            assertThat(actual).isEmpty();
        }
    }

    private Trainer buildTrainer(String firstName, String lastName, String username, Long specializationId) {
        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .password("12345")
                .isActive(true)
                .build();
        TrainingType specialization = TrainingType.builder()
                .id(specializationId)
                .build();

        return Trainer.builder()
                .user(user)
                .specialization(specialization)
                .build();
    }

    private void deleteTrainer(Long id) {
        try (Session session = sessionFactory.openSession()) {
            var transaction = session.beginTransaction();

            Trainer trainer = session.get(Trainer.class, id);

            if (trainer != null) {
                session.remove(trainer);
            }

            transaction.commit();
        }
    }
}