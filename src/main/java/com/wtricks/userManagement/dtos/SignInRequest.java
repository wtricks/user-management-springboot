package com.wtricks.userManagement.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class SignInRequest {
    @Email(message = "Invalid email address")
    @NotBlank(message = "Email cannot be blank")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String password;
}
