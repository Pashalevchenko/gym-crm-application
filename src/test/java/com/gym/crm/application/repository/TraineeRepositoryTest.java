package com.gym.crm.application.repository;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.gym.crm.application.entity.Trainee;
import com.gym.crm.application.entity.Trainer;
import com.gym.crm.application.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@DisplayName("Trainee DAO DBUnit integration tests")
class TraineeRepositoryTest extends AbstractRepositoryTest<TraineeRepository>{

    private static final Long TRAINEE_ID = 1L;
    private static final Long SECOND_TRAINEE_ID = 2L;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Nested
    @DatabaseSetup(value = "/dataset/trainee-data-init.xml", type = DatabaseOperation.CLEAN_INSERT)
    @DisplayName("create")
    class CreateTests {

        @Test
        @DisplayName("Should save trainee")
        void create_success() {
            jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN id RESTART WITH 100");
            jdbcTemplate.execute("ALTER TABLE trainees ALTER COLUMN id RESTART WITH 100");

            Trainee trainee = buildTrainee("New", "Trainee", "new.trainee");
            Trainee actual = repository.save(trainee);

            assertThat(actual).isNotNull();
            assertThat(actual.getId()).isNotNull();
            assertThat(actual.getUser()).isNotNull();
            assertThat(actual.getUser().getId()).isNotNull();

            Optional<Trainee> found = repository.findByUserUsername("new.trainee");

            assertThat(found).isPresent();
        }

        @Test
        @DisplayName("Should throw exception when trainee is null")
        void create_nullTrainee() {
            RuntimeException exception = assertThrows(RuntimeException.class, () -> repository.save(null));

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
            Trainee existing = repository.findById(TRAINEE_ID).orElseThrow();
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

            Trainee updated = repository.save(traineeToUpdate);
            Optional<Trainee> found = repository.findById(updated.getId());

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
            RuntimeException exception = assertThrows(RuntimeException.class, () -> repository.save(null));

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
            Optional<Trainee> found = repository.findById(TRAINEE_ID);

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
            Optional<Trainee> actual = repository.findById(999L);

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
            Optional<Trainee> found = repository.findByUserUsername("marta.muscle");

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
            Optional<Trainee> found = repository.findByUserUsername("ghost.gains");

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
            List<Trainee> actual = repository.findAll();

            assertThat(actual).hasSize(2);
            assertThat(actual)
                    .extracting(trainee -> trainee.getUser().getUsername())
                    .containsExactlyInAnyOrder("borys.burpee", "marta.muscle");
        }
    }

    @Nested
    @DatabaseSetup(value = "/dataset/trainee-data-init.xml", type = DatabaseOperation.CLEAN_INSERT)
    @DisplayName("deleteByUsername")
    class DeleteByUsernameTests {

        @Test
        @DisplayName("Should delete trainee when trainee with requested username exists")
        void deleteByUsername_success() {
            repository.deleteByUserUsername("marta.muscle");

            assertThat(repository.findById(SECOND_TRAINEE_ID)).isEmpty();
            assertThat(repository.findByUserUsername("marta.muscle")).isEmpty();
        }
    }

    @Nested
    @DatabaseSetup(value = "/dataset/trainee-data-init.xml", type = DatabaseOperation.CLEAN_INSERT)
    @DisplayName("findNotAssignedTrainers")
    class FindNotAssignedTrainersTests {

        @Test
        @DisplayName("Should return trainers not assigned to trainee")
        void findNotAssignedTrainers_success() {
            List<Trainer> result = repository.findNotAssignedTrainers("borys.burpee");

            assertThat(result)
                    .extracting(trainer -> trainer.getUser().getUsername())
                    .containsExactlyInAnyOrder("fedir.foamroller", "ira.iron");
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
}