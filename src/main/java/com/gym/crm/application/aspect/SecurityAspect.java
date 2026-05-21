package com.gym.crm.application.aspect;

import com.gym.crm.application.exception.AuthorizationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class SecurityAspect {

    @Before("@annotation(com.gym.crm.application.aspect.annotation.Authenticated)")
    public void authorize(JoinPoint joinPoint) {
        String loggedInUser = getLoggedInUsername();

        if (loggedInUser == null) {
            throw new AuthorizationException("User is not authorized for request operation");
        }

        Object[] args = joinPoint.getArgs();

        if (args.length == 0) {
            return;
        }

        String targetUsername = extractUsername(args[0]);

        if (targetUsername != null && !loggedInUser.equals(targetUsername)) {
            throw new AuthorizationException("User is not authorized for request operation");
        }
    }

    private String getLoggedInUsername() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return null;
        }

        HttpServletRequest request = attributes.getRequest();
        HttpSession session = request.getSession(false);

        if (session == null) {
            return null;
        }

        return (String) session.getAttribute("username");
    }

    private String extractUsername(Object request) {
        if (request instanceof String s) return s;
        try {
            return (String) request.getClass().getMethod("getUsername").invoke(request);
        } catch (Exception e) { return null; }
    }
}