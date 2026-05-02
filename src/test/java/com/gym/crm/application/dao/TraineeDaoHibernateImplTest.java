package com.gym.crm.application.dao;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.gym.crm.application.entity.Trainee;
import com.gym.crm.application.entity.Trainer;
import com.gym.crm.application.entity.User;
import org.hibernate.Session;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import com.gym.crm.application.entity.Training;
import com.gym.crm.application.search.filter.TraineeTrainingSearchFilter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Trainee Hibernate DAO DBUnit integration tests")
class TraineeDaoHibernateImplTest extends AbstractDaoTest<TraineeDaoHibernate> {

    private static final Long TRAINEE_ID = 1L;
    private static final Long SECOND_TRAINEE_ID = 2L;
    private static final Long EXISTING_TRAINER_ID = 1L;
    private static final Long SECOND_TRAINER_ID = 2L;
    private static final Long THIRD_TRAINER_ID = 3L;

    @Nested
    @DatabaseSetup(value = "/dataset/trainee-data-init.xml", type = DatabaseOperation.CLEAN_INSERT)
    @DisplayName("create")
    class CreateTests {

        @Test
        @DisplayName("Should save trainee")
        void create_success() {
            dao.delete(2L);
            dao.delete(1L);

            Trainee trainee = buildTrainee("New", "Trainee", "new.trainee");
            Trainee actual = dao.create(trainee);

            assertThat(actual).isNotNull();
            assertThat(actual.getId()).isNotNull();
            assertThat(actual.getUser()).isNotNull();
            assertThat(actual.getUser().getId()).isNotNull();

            Optional<Trainee> found = dao.findByUsername("new.trainee");

            assertThat(found).isPresent();
        }

        @Test
        @DisplayName("Should throw exception when trainee is null")
        void create_nullTrainee() {
            RuntimeException exception = assertThrows(RuntimeException.class, () -> dao.create(null));

            assertThat(exception).isNotNull();
        }
    }

    @Nested
    @DatabaseSetup(value = "/dataset/trainee-data-init.xml", type = DatabaseOperation.CLEAN_INSERT)
    @DisplayName("update")
    class UpdateTests {

        @Test
        @DisplayName("Should update trainee when trainee exists")
        void update_success() {
            Trainee existing = dao.findById(TRAINEE_ID).orElseThrow();
            User user = User.builder()
                    .id(existing.getUser().getId())
                    .firstName("Updated")
                    .lastName("Burpee")
                    .username(existing.getUser().getUsername())
                    .password(existing.getUser().getPassword())
                    .isActive(existing.getUser().isActive())
                    .build();
            Trainee traineeToUpdate = Trainee.builder()
                    .id(existing.getId())
                    .dateOfBirth(LocalDate.of(1999, 9, 9))
                    .address("Lviv")
                    .user(user)
                    .build();

            Trainee updated = dao.update(traineeToUpdate);
            Optional<Trainee> found = dao.findById(updated.getId());

            assertThat(found).isPresent();

            Trainee actual = found.get();

            assertThat(actual.getId()).isEqualTo(TRAINEE_ID);
            assertThat(actual.getDateOfBirth()).isEqualTo(LocalDate.of(1999, 9, 9));
            assertThat(actual.getAddress()).isEqualTo("Lviv");
            assertThat(actual.getUser().getId()).isEqualTo(TRAINEE_ID);
            assertThat(actual.getUser().getFirstName()).isEqualTo("Updated");
            assertThat(actual.getUser().getLastName()).isEqualTo("Burpee");
            assertThat(actual.getUser().getUsername()).isEqualTo("borys.burpee");
            assertThat(actual.getUser().getPassword()).isEqualTo("12345");
            assertThat(actual.getUser().isActive()).isTrue();
        }

        @Test
        @DisplayName("Should throw exception when trainee is null")
        void update_nullTrainee() {
            RuntimeException exception = assertThrows(RuntimeException.class, () -> dao.update(null));

            assertThat(exception).isNotNull();
        }
    }

    @Nested
    @DatabaseSetup(value = "/dataset/trainee-data-init.xml", type = DatabaseOperation.CLEAN_INSERT)
    @DisplayName("findById")
    class FindByIdTests {

        @Test
        @DisplayName("Should return trainee when trainee with requested id exists")
        void findById_found() {
            Optional<Trainee> found = dao.findById(TRAINEE_ID);

            assertThat(found).isPresent();

            Trainee actual = found.get();

            assertThat(actual.getId()).isEqualTo(TRAINEE_ID);
            assertThat(actual.getDateOfBirth()).isEqualTo(LocalDate.of(2000, 1, 1));
            assertThat(actual.getAddress()).isEqualTo("Kyiv");
            assertThat(actual.getUser().getId()).isEqualTo(TRAINEE_ID);
            assertThat(actual.getUser().getFirstName()).isEqualTo("Borys");
            assertThat(actual.getUser().getLastName()).isEqualTo("Burpee");
            assertThat(actual.getUser().getUsername()).isEqualTo("borys.burpee");
            assertThat(actual.getUser().getPassword()).isEqualTo("12345");
            assertThat(actual.getUser().isActive()).isTrue();
        }

        @Test
        @DisplayName("Should return empty optional when trainee with requested id does not exist")
        void findById_notFound() {
            Optional<Trainee> actual = dao.findById(999L);

            assertThat(actual).isEmpty();
        }
    }

    @Nested
    @DatabaseSetup(value = "/dataset/trainee-data-init.xml", type = DatabaseOperation.CLEAN_INSERT)
    @DisplayName("findByUsername")
    class FindByUsernameTests {

        @Test
        @DisplayName("Should return trainee when trainee with requested username exists")
        void findByUsername_found() {
            Optional<Trainee> found = dao.findByUsername("marta.muscle");

            assertThat(found).isPresent();

            Trainee actual = found.get();

            assertThat(actual.getId()).isEqualTo(SECOND_TRAINEE_ID);
            assertThat(actual.getDateOfBirth()).isEqualTo(LocalDate.of(2001, 2, 2));
            assertThat(actual.getAddress()).isEqualTo("Lviv");
            assertThat(actual.getUser().getId()).isEqualTo(SECOND_TRAINEE_ID);
            assertThat(actual.getUser().getFirstName()).isEqualTo("Marta");
            assertThat(actual.getUser().getLastName()).isEqualTo("Muscle");
            assertThat(actual.getUser().getUsername()).isEqualTo("marta.muscle");
            assertThat(actual.getUser().getPassword()).isEqualTo("12345");
            assertThat(actual.getUser().isActive()).isTrue();
        }

        @Test
        @DisplayName("Should return empty optional when trainee with requested username does not exist")
        void findByUsername_notFound() {
            Optional<Trainee> found = dao.findByUsername("ghost.gains");

            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DatabaseSetup(value = "/dataset/trainee-data-init.xml", type = DatabaseOperation.CLEAN_INSERT)
    @DisplayName("findAll")
    class FindAllTests {

        @Test
        @DisplayName("Should return all trainees")
        void findAll_success() {
            List<Trainee> actual = dao.findAll();

            assertThat(actual).hasSize(2);
            assertThat(actual)
                    .extracting(trainee -> trainee.getUser().getUsername())
                    .containsExactlyInAnyOrder("borys.burpee", "marta.muscle");
        }

        @Test
        @DisplayName("Should return empty list when there are no trainees")
        void findAll_empty() {
            dao.delete(SECOND_TRAINEE_ID);
            dao.delete(TRAINEE_ID);

            List<Trainee> actual = dao.findAll();

            assertThat(actual).isEmpty();
        }
    }

    @Nested
    @DatabaseSetup(value = "/dataset/trainee-data-init.xml", type = DatabaseOperation.CLEAN_INSERT)
    @DisplayName("delete")
    class DeleteTests {

        @Test
        @DisplayName("Should delete trainee when trainee with requested id exists")
        void delete_success() {
            dao.delete(SECOND_TRAINEE_ID);

            assertThat(dao.findById(SECOND_TRAINEE_ID)).isEmpty();
            assertThat(dao.findByUsername("marta.muscle")).isEmpty();
        }

        @Test
        @DisplayName("Should throw exception when trainee with requested id does not exist")
        void delete_notFound() {
            RuntimeException exception = assertThrows(RuntimeException.class, () -> dao.delete(999L));

            assertThat(exception.getMessage()).contains("not found");
        }
    }

    @Nested
    @DatabaseSetup(value = "/dataset/trainee-data-init.xml", type = DatabaseOperation.CLEAN_INSERT)
    @DisplayName("deleteByUsername")
    class DeleteByUsernameTests {

        @Test
        @DisplayName("Should delete trainee when trainee with requested username exists")
        void deleteByUsername_success() {
            dao.deleteByUsername("marta.muscle");

            assertThat(dao.findById(SECOND_TRAINEE_ID)).isEmpty();
            assertThat(dao.findByUsername("marta.muscle")).isEmpty();
        }

        @Test
        @DisplayName("Should throw exception when trainee with requested username does not exist")
        void deleteByUsername_notFound() {
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> dao.deleteByUsername("missing.muscle"));

            assertThat(exception.getMessage()).contains("not found");
        }
    }

    @Nested
    @DatabaseSetup(value = "/dataset/trainee-data-init.xml", type = DatabaseOperation.CLEAN_INSERT)
    @DisplayName("findNotAssignedTrainers")
    class FindNotAssignedTrainersTests {

        @Test
        @DisplayName("Should return trainers not assigned to trainee")
        void findNotAssignedTrainers_success() {
            List<Trainer> result = dao.findNotAssignedTrainers("borys.burpee");

            assertThat(result)
                    .extracting(trainer -> trainer.getUser().getUsername())
                    .containsExactlyInAnyOrder("fedir.foamroller", "ira.iron");
        }

        @Test
        @DisplayName("Should return empty list when all trainers are assigned")
        void findNotAssignedTrainers_empty() {
            Trainer trainerOne = findTrainerById(EXISTING_TRAINER_ID);
            Trainer trainerTwo = findTrainerById(SECOND_TRAINER_ID);
            Trainer trainerThree = findTrainerById(THIRD_TRAINER_ID);

            assertThat(trainerOne).isNotNull();
            assertThat(trainerTwo).isNotNull();
            assertThat(trainerThree).isNotNull();

            dao.updateTrainersList("borys.burpee", Set.of(trainerOne, trainerTwo, trainerThree));

            List<Trainer> result = dao.findNotAssignedTrainers("borys.burpee");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DatabaseSetup(value = "/dataset/trainee-data-init.xml", type = DatabaseOperation.CLEAN_INSERT)
    @DisplayName("updateTrainersList")
    class UpdateTrainersListTests {

        @Test
        @DisplayName("Should replace trainee trainers list with provided trainers")
        void updateTrainersList_success() {
            Trainer trainerOne = findTrainerById(SECOND_TRAINER_ID);
            Trainer trainerTwo = findTrainerById(THIRD_TRAINER_ID);

            assertThat(trainerOne).isNotNull();
            assertThat(trainerTwo).isNotNull();

            dao.updateTrainersList("borys.burpee", Set.of(trainerOne, trainerTwo));

            Trainee found = findTraineeWithTrainersByUsername("borys.burpee");

            assertThat(found).isNotNull();
            assertThat(found.getTrainers()).hasSize(2);
            assertThat(found.getTrainers())
                    .extracting(trainer -> trainer.getUser().getUsername())
                    .containsExactlyInAnyOrder("fedir.foamroller", "ira.iron");
        }

        @Test
        @DisplayName("Should throw exception when trainee with requested username does not exist")
        void updateTrainersList_traineeNotFound() {
            Trainer trainer = findTrainerById(EXISTING_TRAINER_ID);

            assertThat(trainer).isNotNull();

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> dao.updateTrainersList("unknown.unicorn", Set.of(trainer)));

            assertThat(exception.getMessage()).contains("not found");
        }
    }

    @Nested
    @DatabaseSetup(value = "/dataset/trainee-data-init.xml", type = DatabaseOperation.CLEAN_INSERT)
    @DisplayName("findTrainingsByCriteria")
    class FindTrainingsByCriteriaTests {

        @ParameterizedTest(name = "{index} => {0}")
        @MethodSource("com.gym.crm.application.dao.TraineeDaoHibernateImplTest#findTrainingsByCriteriaCases")
        @DisplayName("Should filter trainee trainings by criteria")
        void findTrainingsByCriteria_shouldFilterTrainings(String testCase, TraineeTrainingSearchFilter filter, List<String> expectedTrainingNames) {
            List<Training> actual = dao.findTrainingsByCriteria(filter);

            assertThat(actual)
                    .extracting(Training::getTrainingName)
                    .containsExactlyInAnyOrderElementsOf(expectedTrainingNames);
        }
    }

    private Trainee buildTrainee(String firstName, String lastName, String username) {
        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .password("12345")
                .isActive(true)
                .build();

        return Trainee.builder()
                .user(user)
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("Kyiv")
                .build();
    }

    private Trainer findTrainerById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Trainer.class, id);
        }
    }

    private Trainee findTraineeWithTrainersByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("""
                                       select distinct t
                                       from Trainee t
                                       left join fetch t.trainers trainer
                                       left join fetch trainer.user
                                       where t.user.username = :username
                                       """,
                            Trainee.class)
                    .setParameter("username", username)
                    .uniqueResult();
        }
    }

    private static Stream<Arguments> findTrainingsByCriteriaCases() {
        return Stream.of(Arguments.of("by username", TraineeTrainingSearchFilter.builder()
                                         .username("borys.burpee")
                                         .build(), List.of("Morning Penguin Stretch")),
                         Arguments.of("by username and date range", TraineeTrainingSearchFilter.builder()
                                        .username("borys.burpee")
                                        .fromDate(LocalDate.of(2026, 4, 1))
                                        .toDate(LocalDate.of(2026, 4, 30))
                                        .build(), List.of("Morning Penguin Stretch")),
                         Arguments.of("by username and trainer name", TraineeTrainingSearchFilter.builder()
                                        .username("borys.burpee")
                                        .trainerName("Pavlo Plank")
                                        .build(), List.of("Morning Penguin Stretch")),
                         Arguments.of("by username and training type", TraineeTrainingSearchFilter.builder()
                                        .username("borys.burpee")
                                        .trainingTypeName("Penguin Yoga")
                                        .build(), List.of("Morning Penguin Stretch")),
                         Arguments.of("empty when training type does not match", TraineeTrainingSearchFilter.builder()
                                        .username("borys.burpee")
                                        .trainingTypeName("Strength Shenanigans")
                                        .build(), List.of()),
                         Arguments.of("empty when date range does not match", TraineeTrainingSearchFilter.builder()
                                        .username("borys.burpee")
                                        .fromDate(LocalDate.of(2026, 5, 1))
                                        .toDate(LocalDate.of(2026, 5, 31))
                                        .build(), List.of()),
                         Arguments.of("empty when trainer name does not match", TraineeTrainingSearchFilter.builder()
                                        .username("borys.burpee")
                                        .trainerName("Fedir Foamroller")
                                        .build(), List.of()));
    }
}