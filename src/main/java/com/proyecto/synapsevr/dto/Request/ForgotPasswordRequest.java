package com.proyecto.synapsevr.dto.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request para solicitar recuperación de contraseña")
public class ForgotPasswordRequest {

    @NotBlank(message = "El email es requerido")
    @Email(message = "El formato del email no es válido")
    @Schema(description = "Email del usuario", example = "usuario@ejemplo.com")
    private String email;
}
