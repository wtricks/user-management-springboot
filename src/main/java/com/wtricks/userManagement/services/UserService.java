package com.wtricks.userManagement.services;

import com.wtricks.userManagement.dtos.SignInRequest;
import com.wtricks.userManagement.dtos.SignUpRequest;
import com.wtricks.userManagement.enums.RoleEnum;
import com.wtricks.userManagement.enums.TokenType;
import com.wtricks.userManagement.exceptions.UserAlreadyExist;
import com.wtricks.userManagement.repositories.SessionRepository;
import com.wtricks.userManagement.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.wtricks.userManagement.models.Session;
import com.wtricks.userManagement.models.User;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final SessionRepository sessionRepository;

    @SuppressWarnings("null")
    public User createNewUser(SignUpRequest request) throws UserAlreadyExist {
        Optional<User> user = userRepository.findByEmail(request.getEmail());

        // if user is exists already.
        if (user.isPresent()) {
            throw new UserAlreadyExist("Email address is already taken.");
        }

        // creating new user object
        // we're setting user role is 'user' by default.
        User newUser = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(RoleEnum.USER)
                .isActive(true)
                .build();

        return userRepository.save(newUser);
    }

    @SuppressWarnings("null")
    public String authenticateUser(SignInRequest request) {
        // this will also handle password matching and email matching
        // So we don't need to care about to match user password or email address.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail()).get();

        // generate a token and return it.
        String token = jwtService.generateToken(user);

        // add token in database
        Session session = Session.builder()
            .token(token)
            .user(user)
            .tokenType(TokenType.BEARER)
            .expired(false)
            .build();

        sessionRepository.save(session);

        return token;
    }
}