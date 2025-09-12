package com.proyecto.synapsevr.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    
    private String token;
    private String email;
    private String userName;
    private String role;
    private String message;
    
    // Método para crear respuesta exitosa
    public static AuthResponse success(String token, String email, String userName, String role) {
        return AuthResponse.builder()
                .token(token)
                .email(email)
                .userName(userName)
                .role(role)
                .message("Autenticación exitosa")
                .build();
    }
}