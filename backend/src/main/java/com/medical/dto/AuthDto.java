package com.medical.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class AuthDto {

    @Data
    public static class LoginRequest {
        @NotBlank
        private String username;
        @NotBlank
        private String password;
    }

    @Data
    public static class RegisterRequest {
        @NotBlank
        private String username;
        @NotBlank
        private String password;
        @Email
        private String email;
        private String role; // ADMIN, DOCTOR, PATIENT
        private Long profileId;
    }

    @Data
    public static class JwtResponse {
        private String token;
        private String type = "Bearer";
        private Long id;
        private String username;
        private String email;
        private String role;
        private Long profileId;

        public JwtResponse(String token, Long id, String username, String email, String role, Long profileId) {
            this.token = token;
            this.id = id;
            this.username = username;
            this.email = email;
            this.role = role;
            this.profileId = profileId;
        }
    }
}
