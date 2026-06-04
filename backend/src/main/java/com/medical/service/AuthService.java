package com.medical.service;

import com.medical.dto.AuthDto;
import com.medical.entity.Role;
import com.medical.entity.User;
import com.medical.exception.DuplicateResourceException;
import com.medical.repository.UserRepository;
import com.medical.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    public AuthDto.JwtResponse login(AuthDto.LoginRequest request) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        String token = jwtUtils.generateJwtToken(auth);
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        return new AuthDto.JwtResponse(token, user.getId(), user.getUsername(), user.getEmail(), user.getRole().name());
    }

    public void register(AuthDto.RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new DuplicateResourceException("Username already taken: " + request.getUsername());
        if (userRepository.existsByEmail(request.getEmail()))
            throw new DuplicateResourceException("Email already in use: " + request.getEmail());
        User user = User.builder()
                .username(request.getUsername())
                .password(encoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(Role.valueOf(request.getRole().toUpperCase()))
                .profileId(request.getProfileId())
                .build();
        userRepository.save(user);
    }
}
