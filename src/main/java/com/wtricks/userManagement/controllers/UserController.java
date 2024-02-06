package com.wtricks.userManagement.controllers;

import com.wtricks.userManagement.dtos.ResponseDto;
import com.wtricks.userManagement.dtos.SignInRequest;
import com.wtricks.userManagement.dtos.SignUpRequest;
import com.wtricks.userManagement.exceptions.UserAlreadyExist;
import com.wtricks.userManagement.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ResponseDto> createNewUser(@Valid @RequestBody SignUpRequest request) {
        ResponseDto response = new ResponseDto();

        try {
            userService.createNewUser(request);
            response.setStatus(HttpStatus.ACCEPTED);
        } catch (UserAlreadyExist e) {
            response.setStatus(HttpStatus.CONFLICT);
            response.setMessage(e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/signin")
    public ResponseEntity<ResponseDto> authenticateUser(@Valid @RequestBody SignInRequest request) {
        ResponseDto response = new ResponseDto();
        try {
            response.setData(userService.authenticateUser(request));
            response.setStatus(HttpStatus.ACCEPTED);
        } catch (Exception e) {
            response.setData(e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(response);
    }
}
