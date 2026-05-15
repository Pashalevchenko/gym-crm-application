package com.gym.crm.application.dao;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.gym.crm.application.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("User DAO DBUnit integration tests")
class UserDaoImplTest extends AbstractDaoTest<UserDao> {

    private static final String EXISTING_USERNAME = "borys.burpee";
    private static final String SECOND_EXISTING_USERNAME = "marta.muscle";
    private static final String MISSING_USERNAME = "ghost.gains";

    @Nested
    @DatabaseSetup(value = "/dataset/user-data-init.xml", type = DatabaseOperation.CLEAN_INSERT)
    @DisplayName("save")
    class SaveTests {

        @Test
        @DisplayName("Should save user")
        void save_success() {
            User user = User.builder()
                    .firstName("New")
                    .lastName("User")
                    .username("new.user")
                    .password("12345")
                    .isActive(true)
                    .build();

            User actual = dao.save(user);
            assertThat(actual).isNotNull();
            assertThat(actual.getId()).isNotNull();

            Optional<User> found = dao.findByUsername("new.user");
            assertThat(found).isPresent();
            assertThat(found.get().getUsername()).isEqualTo("new.user");
            assertThat(found.get().getFirstName()).isEqualTo("New");
            assertThat(found.get().getLastName()).isEqualTo("User");
            assertThat(found.get().getPassword()).isEqualTo("12345");
            assertThat(found.get().isActive()).isTrue();
        }

        @Test
        @DisplayName("Should throw exception when user is null")
        void save_nullUser() {
            RuntimeException exception = assertThrows(RuntimeException.class, () -> dao.save(null));

            assertThat(exception).isNotNull();
        }
    }

    @Nested
    @DatabaseSetup(value = "/dataset/user-data-init.xml", type = DatabaseOperation.CLEAN_INSERT)
    @DisplayName("update")
    class UpdateTests {

        @Test
        @DisplayName("Should update user when user exists")
        void update_success() {
            User existing = dao.findByUsername(EXISTING_USERNAME).orElseThrow();
            User userToUpdate = existing.toBuilder()
                    .firstName("Updated")
                    .lastName("Burpee")
                    .password("new-password")
                    .isActive(false)
                    .build();

            User actual = dao.update(userToUpdate);

            assertThat(actual.getId()).isEqualTo(existing.getId());
            assertThat(actual.getUsername()).isEqualTo(EXISTING_USERNAME);
            assertThat(actual.getFirstName()).isEqualTo("Updated");
            assertThat(actual.getLastName()).isEqualTo("Burpee");
            assertThat(actual.getPassword()).isEqualTo("new-password");
            assertThat(actual.isActive()).isFalse();
        }

        @Test
        @DisplayName("Should throw exception when user is null")
        void update_nullUser() {
            RuntimeException exception = assertThrows(RuntimeException.class, () -> dao.update(null));

            assertThat(exception).isNotNull();
        }
    }

    @Nested
    @DatabaseSetup(value = "/dataset/user-data-init.xml", type = DatabaseOperation.CLEAN_INSERT)
    @DisplayName("findByUsername")
    class FindByUsernameTests {

        @Test
        @DisplayName("Should return user when user with requested username exists")
        void findByUsername_found() {
            User actual = dao.findByUsername(SECOND_EXISTING_USERNAME).get();

            assertThat(actual.getId()).isEqualTo(12L);
            assertThat(actual.getUsername()).isEqualTo("marta.muscle");
            assertThat(actual.getFirstName()).isEqualTo("Marta");
            assertThat(actual.getLastName()).isEqualTo("Muscle");
            assertThat(actual.getPassword()).isEqualTo("12345");
            assertThat(actual.isActive()).isTrue();
        }

        @Test
        @DisplayName("Should return empty optional when user with requested username does not exist")
        void findByUsername_notFound() {
            Optional<User> found = dao.findByUsername(MISSING_USERNAME);

            assertThat(found).isEmpty();
        }
    }
}