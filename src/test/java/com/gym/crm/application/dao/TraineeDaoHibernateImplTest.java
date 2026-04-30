package com.gym.crm.application.dao;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import com.gym.crm.application.entity.Trainee;
import com.gym.crm.application.entity.Trainer;
import com.gym.crm.application.entity.Training;
import com.gym.crm.application.entity.User;
import org.hibernate.Session;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Trainee Hibernate DAO DBUnit integration tests")
class TraineeDaoHibernateImplTest extends AbstractDaoIntegrationTest {
    private final Long TRAINEE_ID = 1L;

    @Nested
    @DisplayName("create")
    class CreateTests {

        @Test
        @DisplayName("Should save trainee with all fields")
        void create_success() {
            Trainee trainee = buildTrainee("Borys", "Burpee", "borys.burpee");
            Trainee created = traineeDao.create(trainee);

            assertThat(created.getId()).isNotNull();
            assertThat(created.getUser().getId()).isNotNull();

            Optional<Trainee> found = traineeDao.findById(created.getId());

            assertThat(found).isPresent();

            Trainee actual = found.get();

            assertThat(actual.getId()).isEqualTo(created.getId());
            assertThat(actual.getDateOfBirth()).isEqualTo(LocalDate.of(2000, 1, 1));
            assertThat(actual.getAddress()).isEqualTo("Kyiv");
            assertThat(actual.getUser()).isNotNull();
            assertThat(actual.getUser().getId()).isNotNull();
            assertThat(actual.getUser().getFirstName()).isEqualTo("Borys");
            assertThat(actual.getUser().getLastName()).isEqualTo("Burpee");
            assertThat(actual.getUser().getUsername()).isEqualTo("borys.burpee");
            assertThat(actual.getUser().getPassword()).isEqualTo("12345");
            assertThat(actual.getUser().isActive()).isTrue();
        }

        @Test
        @DisplayName("Should throw exception when trainee is null")
        void create_nullTrainee() {
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> traineeDao.create(null));

            assertThat(exception).isNotNull();
        }
    }

    @Nested
    @DatabaseSetup("/datasets/trainee-dataset.xml")
    @DisplayName("update")
    class UpdateTests {

        @Test
        @DisplayName("Should update trainee when trainee exists")
        void update_success() {
            Trainee trainee = traineeDao.findById(TRAINEE_ID).orElseThrow();
            trainee.setDateOfBirth(LocalDate.of(1999, 9, 9));
            trainee.setAddress("Lviv");
            trainee.getUser().setFirstName("Updated");
            trainee.getUser().setLastName("Burpee");

            Trainee updated = traineeDao.update(trainee);
            Optional<Trainee> found = traineeDao.findById(updated.getId());

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
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> traineeDao.update(null));

            assertThat(exception).isNotNull();
        }
    }

    @Nested
    @DatabaseSetup("/datasets/trainee-dataset.xml")
    @DisplayName("findById")
    class FindByIdTests {

        @Test
        @DisplayName("Should return trainee when trainee with requested id exists")
        void findById_found() {
            Optional<Trainee> found = traineeDao.findById(TRAINEE_ID);

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
            Optional<Trainee> found = traineeDao.findById(999L);

            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DatabaseSetup("/datasets/trainee-dataset.xml")
    @DisplayName("findByUsername")
    class FindByUsernameTests {

        @Test
        @DisplayName("Should return trainee when trainee with requested username exists")
        void findByUsername_found() {
            Optional<Trainee> found = traineeDao.findByUsername("marta.muscle");

            assertThat(found).isPresent();

            Trainee actual = found.get();

            assertThat(actual.getId()).isEqualTo(2L);
            assertThat(actual.getDateOfBirth()).isEqualTo(LocalDate.of(2001, 2, 2));
            assertThat(actual.getAddress()).isEqualTo("Lviv");
            assertThat(actual.getUser().getId()).isEqualTo(2L);
            assertThat(actual.getUser().getFirstName()).isEqualTo("Marta");
            assertThat(actual.getUser().getLastName()).isEqualTo("Muscle");
            assertThat(actual.getUser().getUsername()).isEqualTo("marta.muscle");
            assertThat(actual.getUser().getPassword()).isEqualTo("12345");
            assertThat(actual.getUser().isActive()).isTrue();
        }

        @Test
        @DisplayName("Should return empty optional when trainee with requested username does not exist")
        void findByUsername_notFound() {
            Optional<Trainee> found = traineeDao.findByUsername("ghost.gains");

            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAll")
    class FindAllTests {

        @Test
        @DatabaseSetup("/datasets/trainee-dataset.xml")
        @DisplayName("Should return all trainees")
        void findAll_success() {
            List<Trainee> trainees = traineeDao.findAll();

            assertThat(trainees).hasSize(2);
            assertThat(trainees)
                    .extracting(trainee -> trainee.getUser().getUsername())
                    .containsExactlyInAnyOrder("borys.burpee", "marta.muscle");
        }

        @Test
        @DatabaseSetup(value = "/datasets/clean-database.xml", type = DatabaseOperation.DELETE_ALL)
        @DisplayName("Should return empty list when there are no trainees")
        void findAll_empty() {
            List<Trainee> trainees = traineeDao.findAll();

            assertThat(trainees).isEmpty();
        }
    }

    @Nested
    @DatabaseSetup("/datasets/trainee-dataset.xml")
    @DisplayName("delete")
    class DeleteTests {

        @Test
        @DisplayName("Should delete trainee when trainee with requested id exists")
        void delete_success() {
            traineeDao.delete(2L);

            assertThat(traineeDao.findById(2L)).isEmpty();
            assertThat(traineeDao.findByUsername("marta.muscle")).isEmpty();
        }

        @Test
        @DisplayName("Should throw exception when trainee with requested id does not exist")
        void delete_notFound() {
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> traineeDao.delete(999L));

            assertThat(exception.getMessage()).contains("not found");
        }
    }

    @Nested
    @DatabaseSetup("/datasets/trainee-dataset.xml")
    @DisplayName("deleteByUsername")
    class DeleteByUsernameTests {

        @Test
        @DisplayName("Should delete trainee when trainee with requested username exists")
        void deleteByUsername_success() {
            traineeDao.deleteByUsername("marta.muscle");

            assertThat(traineeDao.findById(2L)).isEmpty();
            assertThat(traineeDao.findByUsername("marta.muscle")).isEmpty();
        }

        @Test
        @DisplayName("Should throw exception when trainee with requested username does not exist")
        void deleteByUsername_notFound() {
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> traineeDao.deleteByUsername("missing.muscle"));

            assertThat(exception.getMessage()).contains("not found");
        }
    }

    @Nested
    @DatabaseSetup("/datasets/trainee-dataset.xml")
    @DisplayName("findTrainingsByCriteria")
    class FindTrainingsByCriteriaTests {

        @Test
        @DisplayName("Should return trainings matching criteria")
        void findTrainingsByCriteria_success() {
            List<Training> trainings = traineeDao.findTrainingsByCriteria("borys.burpee",
                                                                          LocalDate.of(2026, 4, 1),
                                                                          LocalDate.of(2026, 4, 30),
                                                                          "Pavlo Plank",
                                                                          "Penguin Yoga");

            assertThat(trainings).hasSize(1);

            Training actual = trainings.get(0);

            assertThat(actual.getId()).isEqualTo(TRAINEE_ID);
            assertThat(actual.getTrainingName()).isEqualTo("Morning Penguin Stretch");
            assertThat(actual.getTrainingDate()).isEqualTo(LocalDate.of(2026, 4, 10));
            assertThat(actual.getTrainingDuration()).isEqualTo(60);
        }

        @Test
        @DisplayName("Should return empty list when criteria do not match")
        void findTrainingsByCriteria_noMatch() {
            List<Training> trainings = traineeDao.findTrainingsByCriteria("borys.burpee",
                                                                          LocalDate.of(2026, 5, 1),
                                                                          LocalDate.of(2026, 5, 30),
                                                                          null,
                                                                          null);

            assertThat(trainings).isEmpty();
        }
    }

    @Nested
    @DisplayName("findNotAssignedTrainers")
    class FindNotAssignedTrainersTests {

        @Test
        @DatabaseSetup("/datasets/trainee-dataset.xml")
        @DisplayName("Should return trainers not assigned to trainee")
        void findNotAssignedTrainers_success() {
            List<Trainer> result = traineeDao.findNotAssignedTrainers("borys.burpee");

            assertThat(result)
                    .extracting(trainer -> trainer.getUser().getUsername())
                    .containsExactlyInAnyOrder("fedir.foamroller", "ira.iron");
        }

        @Test
        @DatabaseSetup("/datasets/all-trainers-assigned-dataset.xml")
        @DisplayName("Should return empty list when all trainers are assigned")
        void findNotAssignedTrainers_empty() {
            List<Trainer> result = traineeDao.findNotAssignedTrainers("borys.burpee");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DatabaseSetup("/datasets/trainee-dataset.xml")
    @DisplayName("updateTrainersList")
    class UpdateTrainersListTests {

        @Test
        @DisplayName("Should replace trainee trainers list with provided trainers")
        void updateTrainersList_success() {
            Trainer trainerOne = findTrainerById(2L);
            Trainer trainerTwo = findTrainerById(3L);

            traineeDao.updateTrainersList("borys.burpee", Set.of(trainerOne, trainerTwo));

            Trainee found = findTraineeWithTrainersByUsername("borys.burpee");

            assertThat(found.getTrainers()).hasSize(2);
            assertThat(found.getTrainers())
                    .extracting(trainer -> trainer.getUser().getUsername())
                    .containsExactlyInAnyOrder("fedir.foamroller", "ira.iron");
        }

        @Test
        @DisplayName("Should throw exception when trainee with requested username does not exist")
        void updateTrainersList_traineeNotFound() {
            Trainer trainer = findTrainerById(TRAINEE_ID);

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> traineeDao.updateTrainersList("unknown.unicorn", Set.of(trainer)));

            assertThat(exception.getMessage()).contains("not found");
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
}