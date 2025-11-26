package com.proyecto.synapsevr.dto.Response;

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
    private Integer userId;  // ← AGREGADO: Campo userId crítico para el frontend
    private String message;
    
    // Método para crear respuesta exitosa
    public static AuthResponse success(String token, String email, String userName, String role, Integer userId) {
        return AuthResponse.builder()
                .token(token)
                .email(email)
                .userName(userName)
                .role(role)
                .userId(userId)  // ← AGREGADO: Incluir userId en la respuesta
                .message("Autenticación exitosa")
                .build();
    }
}