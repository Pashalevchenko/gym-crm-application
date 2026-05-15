package com.gym.crm.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.application.facade.GymAppFacade;
import com.gym.crm.application.openapi.LoginChangeRequest;
import com.gym.crm.application.openapi.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private static final String USERNAME = "test.user";
    private static final String PASSWORD = "password";
    private static final String NEW_PASSWORD = "newPassword";
    private static final String BASE_URL = "/api/v1/auth";

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mockMvc;

    @Mock
    private GymAppFacade facade;

    @BeforeEach
    void setUp() {
        AuthController controller = new AuthController(facade);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void login_shouldReturnOk() throws Exception {
        LoginRequest request = new LoginRequest()
                .username(USERNAME)
                .password(PASSWORD);

        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        verify(facade).login(USERNAME, PASSWORD);
    }

    @Test
    void changePassword_shouldReturnOk() throws Exception {
        LoginChangeRequest request = new LoginChangeRequest()
                .username(USERNAME)
                .oldPassword(PASSWORD)
                .newPassword(NEW_PASSWORD);

        mockMvc.perform(put(BASE_URL + "/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(facade).changePassword(any(LoginChangeRequest.class));
    }
}