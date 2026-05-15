package com.gym.crm.application.controller;

import com.gym.crm.application.facade.GymAppFacade;
import com.gym.crm.application.openapi.LoginChangeRequest;
import com.gym.crm.application.openapi.LoginRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private GymAppFacade facade;

    @InjectMocks
    private AuthController authController;

    @Test
    void loginShouldCallFacadeAndReturnOk() {
        LoginRequest request = new LoginRequest()
                .username("test.user")
                .password("12345");
        ResponseEntity<Void> response = authController.login(request);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNull();

        verify(facade).login("test.user", "12345");
    }

    @Test
    void changePasswordShouldCallFacadeAndReturnOk() {
        LoginChangeRequest request = new LoginChangeRequest()
                .username("test.user")
                .oldPassword("old-password")
                .newPassword("new-password");
        ResponseEntity<Void> response = authController.changePassword(request);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNull();

        verify(facade).changePassword(request);
    }
}
