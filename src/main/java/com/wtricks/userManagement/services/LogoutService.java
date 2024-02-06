package com.wtricks.userManagement.services;

import com.wtricks.userManagement.repositories.SessionRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final SessionRepository sessionRepository;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }

        jwt = authHeader.substring(7);
        com.wtricks.userManagement.models.Session storedToken = sessionRepository.findByToken(jwt).orElse(null);

        if (storedToken != null) {
            sessionRepository.delete(storedToken);
            SecurityContextHolder.clearContext();
        }
    }
}