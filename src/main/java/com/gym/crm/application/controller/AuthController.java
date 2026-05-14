package com.gym.crm.application.controller;

import com.gym.crm.application.AuthApi;
import com.gym.crm.application.facade.GymAppFacade;
import com.gym.crm.application.openapi.LoginChangeRequest;
import com.gym.crm.application.openapi.LoginRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final GymAppFacade gymAppFacade;

    @Override
    public ResponseEntity<Void> login(LoginRequest request) {
        gymAppFacade.login(request.getUsername(), request.getPassword());

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> changePassword(LoginChangeRequest request) {
        gymAppFacade.changePassword(request);

        return ResponseEntity.ok().build();
    }


}
