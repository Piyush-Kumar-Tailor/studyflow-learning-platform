package com.studyflow.backend.auth.service;

import com.studyflow.backend.auth.dto.AuthResponse;
import com.studyflow.backend.auth.dto.LoginRequest;
import com.studyflow.backend.auth.dto.RegisterRequest;
import com.studyflow.backend.security.JwtService;
import com.studyflow.backend.user.entity.AppUser;
import com.studyflow.backend.user.repository.UserRepository;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {

        String normalizedEmail = normalizeEmail(request.email());

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException(
                    "An account with this email already exists."
            );
        }

        AppUser user = new AppUser(
                request.name().trim(),
                normalizedEmail,
                passwordEncoder.encode(request.password())
        );

        AppUser savedUser = userRepository.save(user);

        return new AuthResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole().name(),
                null,
                "Registration successful."
        );
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {

        String normalizedEmail = normalizeEmail(request.email());

        AppUser user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new BadCredentialsException(
                        "Invalid email or password."
                ));

        if (!user.isEnabled()
                || !passwordEncoder.matches(
                request.password(),
                user.getPasswordHash()
        )) {

            throw new BadCredentialsException(
                    "Invalid email or password."
            );
        }

        String token = jwtService.generateToken(user);

        return new AuthResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                token,
                "Login successful."
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}