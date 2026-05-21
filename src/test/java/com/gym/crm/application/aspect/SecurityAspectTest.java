package com.gym.crm.application.aspect;

import com.gym.crm.application.exception.AuthorizationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SecurityAspectTest {

    private static final String USERNAME = "test.user";

    private final SecurityAspect securityAspect = new SecurityAspect();

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Should allow access when user is logged in")
    void authorize_whenUserIsLoggedIn_shouldAllowAccess() {
        mockSession(USERNAME);
        JoinPoint joinPoint = mock(JoinPoint.class);

        when(joinPoint.getArgs()).thenReturn(new Object[]{});

        assertDoesNotThrow(() -> securityAspect.authorize(joinPoint));
    }

    @Test
    @DisplayName("Should allow access when logged user matches target username")
    void authorize_whenLoggedUserMatchesTargetUsername_shouldAllowAccess() {
        mockSession(USERNAME);
        JoinPoint joinPoint = mock(JoinPoint.class);

        when(joinPoint.getArgs()).thenReturn(new Object[]{USERNAME});

        assertDoesNotThrow(() -> securityAspect.authorize(joinPoint));
    }

    @Test
    @DisplayName("Should throw exception when no request attributes exist")
    void authorize_whenNoRequestAttributes_shouldThrowException() {
        RequestContextHolder.resetRequestAttributes();
        JoinPoint joinPoint = mock(JoinPoint.class);

        when(joinPoint.getArgs()).thenReturn(new Object[]{USERNAME});

        assertThrows(AuthorizationException.class, () -> securityAspect.authorize(joinPoint));
    }

    @Test
    @DisplayName("Should throw exception when session does not exist")
    void authorize_whenSessionDoesNotExist_shouldThrowException() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        JoinPoint joinPoint = mock(JoinPoint.class);

        when(request.getSession(false)).thenReturn(null);

        RequestContextHolder.setRequestAttributes(attributes);

        when(joinPoint.getArgs()).thenReturn(new Object[]{USERNAME});

        assertThrows(AuthorizationException.class, () -> securityAspect.authorize(joinPoint));
    }

    @Test
    @DisplayName("Should throw exception when logged user is missing")
    void authorize_whenLoggedUserIsMissing_shouldThrowException() {
        mockSession(null);

        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{USERNAME});

        assertThrows(AuthorizationException.class, () -> securityAspect.authorize(joinPoint));
    }

    @Test
    @DisplayName("Should throw exception when logged user does not match target username")
    void authorize_whenLoggedUserDoesNotMatchTargetUsername_shouldThrowException() {
        mockSession("other.user");

        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{USERNAME});

        assertThrows(AuthorizationException.class, () -> securityAspect.authorize(joinPoint));
    }

    private void mockSession(String username) {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("username")).thenReturn(username);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getSession(false)).thenReturn(session);

        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);
    }
}