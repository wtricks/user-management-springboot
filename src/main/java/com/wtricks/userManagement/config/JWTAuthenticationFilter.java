package com.wtricks.userManagement.config;

import com.wtricks.userManagement.repositories.SessionRepository;
import com.wtricks.userManagement.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.wtricks.userManagement.models.Session;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final SessionRepository sessionRepository;

    @Override
    @SuppressWarnings("null")
    protected void doFilterInternal(
            HttpServletRequest req,
            @NotNull HttpServletResponse res,
            @NotNull FilterChain chain
    ) throws ServletException, IOException {
        final String authHeader = req.getHeader("Authorization");
        final String jwt;
        
        // check 'Authorization' header.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(req, res);
            return;
        }

        // get token from header.
        jwt = authHeader.substring(7);

        // get token related info from db
        Session oldTokenFromDb = sessionRepository.findByToken(jwt).orElse(null);

        // check if user is authenticated
        if (oldTokenFromDb != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = oldTokenFromDb.getUser();

            // checking if token is valid
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );
                
                authenticationToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(req)
                );

                logger.debug(authenticationToken);

                // at the last we're setting authentication context
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        chain.doFilter(req, res);
    }
}