package com.gym.crm.application.service.impl;

import com.gym.crm.application.dao.UserDao;
import com.gym.crm.application.entity.User;
import com.gym.crm.application.openapi.LoginChangeRequest;
import com.gym.crm.application.validation.TrainingValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.NoSuchElementException;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private TrainingValidator validator;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void changePasswordShouldUpdatePasswordWhenOldPasswordIsCorrect() {
        LoginChangeRequest request = new LoginChangeRequest()
                .username("ricardo.milos")
                .oldPassword("old-password")
                .newPassword("new-password");
        User existingUser = User.builder()
                .id(1L)
                .firstName("Ricardo")
                .lastName("Milos")
                .username("ricardo.milos")
                .password("encoded-old-password")
                .isActive(true)
                .build();

        when(userDao.findByUsername("ricardo.milos"))
                .thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("old-password", "encoded-old-password"))
                .thenReturn(true);
        when(passwordEncoder.encode("new-password"))
                .thenReturn("encoded-new-password");

        userService.changePassword(request);

        verify(userDao).update(argThat(updatedUser ->
                updatedUser.getUsername().equals("ricardo.milos")
                        && updatedUser.getPassword().equals("encoded-new-password")));
    }

    @Test
    void changePasswordShouldThrowEntityNotFoundExceptionWhenUserDoesNotExist() {
        LoginChangeRequest request = new LoginChangeRequest()
                .username("unknown.user")
                .oldPassword("old-password")
                .newPassword("new-password");

        when(userDao.findByUsername("unknown.user"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.changePassword(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("User not found");

        verify(userDao, never()).update(any());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void changePasswordShouldThrowIllegalArgumentExceptionWhenOldPasswordIsInvalid() {
        LoginChangeRequest request = new LoginChangeRequest()
                .username("ricardo.milos")
                .oldPassword("wrong-password")
                .newPassword("new-password");
        User existingUser = User.builder()
                .id(1L)
                .firstName("Ricardo")
                .lastName("Milos")
                .username("ricardo.milos")
                .password("encoded-old-password")
                .isActive(true)
                .build();

        when(userDao.findByUsername("ricardo.milos"))
                .thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("wrong-password", "encoded-old-password"))
                .thenReturn(false);

        assertThatThrownBy(() -> userService.changePassword(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid password");

        verify(userDao, never()).update(any());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void findByUsernameShouldReturnUserWhenUserExists() {
        User user = User.builder()
                .id(1L)
                .firstName("Ricardo")
                .lastName("Milos")
                .username("ricardo.milos")
                .password("encoded-password")
                .isActive(true)
                .build();

        when(userDao.findByUsername("ricardo.milos"))
                .thenReturn(Optional.of(user));

        User result = userService.findByUsername("ricardo.milos");

        assertThat(result).isEqualTo(user);
    }

    @Test
    void findByUsernameShouldThrowNoSuchElementExceptionWhenUserDoesNotExist() {
        when(userDao.findByUsername("unknown.user"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByUsername("unknown.user"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("User not found");
    }
}