package com.gym.crm.application.aspect;

import com.gym.crm.application.context.SecurityContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class SecurityAspect {

    @Before("@annotation(com.gym.crm.application.annotation.Authenticated) && args(request, ..)")
    public void authorize(Object request) {
        String loggedInUser = SecurityContextHolder.getContext();

        if (loggedInUser == null) {
            throw new SecurityException("Authentication required!");
        }

        String targetUsername = extractUsername(request);
        if (targetUsername != null && !loggedInUser.equals(targetUsername)) {
            throw new SecurityException("Access Denied: You cannot modify other users' data!");
        }
    }

    private String extractUsername(Object request) {
        if (request instanceof String s) return s;
        try {
            return (String) request.getClass().getMethod("getUsername").invoke(request);
        } catch (Exception e) { return null; }
    }
}
