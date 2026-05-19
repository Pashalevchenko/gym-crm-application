package com.gym.crm.application.controller;

import com.gym.crm.application.facade.GymAppFacade;
import com.gym.crm.application.openapi.LoginChangeRequest;
import com.gym.crm.application.openapi.LoginRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final GymAppFacade facade;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest request, HttpSession session) {
        facade.login(request.getUsername(), request.getPassword());

        session.setAttribute("username", request.getUsername());

        return ResponseEntity.ok().build();
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(@RequestBody LoginChangeRequest request) {
        facade.changePassword(request);

        return ResponseEntity.ok().build();
    }
}
