package com.devsense.service;

import com.devsense.model.dto.*;
import com.devsense.model.entity.User;
import com.devsense.model.enums.Plan;
import com.devsense.repository.UserRepository;
import com.devsense.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j  // Lombok: gives us 'log' variable — log.info(), log.error() etc.
public class AuthService {

    private final UserRepository       userRepository;
    private final PasswordEncoder      passwordEncoder;
    private final JwtTokenProvider     jwtProvider;
    private final AuthenticationManager authManager;

    @Transactional
    // @Transactional: if anything fails inside this method, ALL DB changes roll back
    public AuthResponseDto register(RegisterRequestDto req) {
        // Check if email is already taken
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + req.getEmail());
        }

        // Build and save the new User
        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                // encode() hashes the password with BCrypt — NEVER store plain text
                .fullName(req.getFullName())
                .plan(Plan.FREE)
                .reviewsUsed(0)
                .reviewsLimit(5)
                .build();

        userRepository.save(user);
        log.info("New user registered: {}", req.getEmail());

        // Generate JWT for the new user
        String token = jwtProvider.generateToken(user.getEmail());

        return AuthResponseDto.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .email(user.getEmail())
                .fullName(user.getFullName())
                .plan(user.getPlan().name())
                .build();
    }

    public AuthResponseDto login(LoginRequestDto req) {
        try {
            // authManager.authenticate() calls UserDetailsServiceImpl.loadUserByUsername()
            // then checks passwordEncoder.matches(rawPassword, encodedPassword)
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            req.getEmail(),
                            req.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid email or password");
        }

        // If we reach here, credentials are valid — generate token
        User user = userRepository.findByEmail(req.getEmail()).orElseThrow();
        String token = jwtProvider.generateToken(user.getEmail());

        return AuthResponseDto.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .email(user.getEmail())
                .fullName(user.getFullName())
                .plan(user.getPlan().name())
                .build();
    }
}

