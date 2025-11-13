package com.proyecto.synapsevr.dto.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "Request para resetear contraseña con código de verificación")
public class ResetPasswordRequest {

    @NotBlank(message = "El email es requerido")
    @Email(message = "El formato del email no es válido")
    @Schema(description = "Email del usuario", example = "usuario@ejemplo.com")
    private String email;

    @NotBlank(message = "El código de verificación es requerido")
    @Pattern(regexp = "^[0-9]{6}$", message = "El código debe ser de 6 dígitos")
    @Schema(description = "Código de verificación de 6 dígitos", example = "123456")
    private String verificationCode;

    @NotBlank(message = "La nueva contraseña es requerida")
    @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", 
             message = "La contraseña debe contener al menos: 1 minúscula, 1 mayúscula, 1 número y 1 carácter especial")
    @Schema(description = "Nueva contraseña segura", example = "NuevaPassword123!")
    private String newPassword;

    @NotBlank(message = "La confirmación de contraseña es requerida")
    @Schema(description = "Confirmación de la nueva contraseña", example = "NuevaPassword123!")
    private String confirmPassword;

    public boolean isPasswordMatching() {
        return newPassword != null && newPassword.equals(confirmPassword);
    }
}
