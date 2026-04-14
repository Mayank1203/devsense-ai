package com.devsense.controller;

import com.devsense.model.dto.*;
import com.devsense.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
// @RestController = @Controller + @ResponseBody
// Every method returns JSON automatically (no need for @ResponseBody on each method)

@RequestMapping("/api/v1/auth")
// All endpoints in this controller start with /api/v1/auth

@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    // Full URL: POST http://localhost:8080/api/v1/auth/register
    public ResponseEntity<AuthResponseDto> register(
            @Valid @RequestBody RegisterRequestDto req) {
        // @Valid triggers Bean Validation — checks @NotBlank @Email @Size etc.
        // @RequestBody reads JSON from the request body and maps it to RegisterRequestDto

        AuthResponseDto response = authService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
        // 201 Created — new resource (user account) was created
    }

    @PostMapping("/login")
    // Full URL: POST http://localhost:8080/api/v1/auth/login
    public ResponseEntity<AuthResponseDto> login(
            @Valid @RequestBody LoginRequestDto req) {

        return ResponseEntity.ok(authService.login(req));
        // 200 OK — login is not creating a new resource, just authenticating
    }
}
