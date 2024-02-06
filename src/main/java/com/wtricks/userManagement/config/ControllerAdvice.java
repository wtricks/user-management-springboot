package com.wtricks.userManagement.config;

import com.wtricks.userManagement.dtos.ResponseDto;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@org.springframework.web.bind.annotation.ControllerAdvice
@Configuration
public class ControllerAdvice {
     @ExceptionHandler(MethodArgumentNotValidException.class)
     public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
         Map<String, String> errors = new HashMap<>();

         ex.getBindingResult().getAllErrors().forEach((error) -> {
             String name = ((FieldError) error).getField();
             String message = error.getDefaultMessage();
             errors.put(name, message);
         });

         ResponseDto response = new ResponseDto(errors, HttpStatus.BAD_REQUEST, null);
         return ResponseEntity.ok(response);
     }

     @ExceptionHandler(AccessDeniedException.class)
     public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
         ResponseDto response = new ResponseDto("Access denied", HttpStatus.FORBIDDEN, null);
         return ResponseEntity.ok(response);
     }
}
