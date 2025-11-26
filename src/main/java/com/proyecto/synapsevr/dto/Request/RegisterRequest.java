package com.proyecto.synapsevr.dto.Request;

import com.proyecto.synapsevr.Entity.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Request para registro de usuario con validaciones completas")
public class RegisterRequest {

    @NotBlank(message = "El nombre de usuario es requerido")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s.]+$", message = "El nombre solo puede contener letras, espacios y puntos")
    @Schema(description = "Nombre completo del usuario", example = "Dr. Juan Pérez")
    private String userName;

    @NotBlank(message = "El email es requerido")
    @Email(message = "El formato del email no es válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    @Schema(description = "Email del usuario (debe ser único)", example = "juan.perez@hospital.com")
    private String email;

    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", 
             message = "La contraseña debe contener al menos: 1 minúscula, 1 mayúscula, 1 número y 1 carácter especial")
    @Schema(description = "Contraseña segura", example = "MiPassword123!")
    private String password;

    @NotBlank(message = "La confirmación de contraseña es requerida")
    @Schema(description = "Confirmación de contraseña", example = "MiPassword123!")
    private String confirmPassword;

    @Pattern(regexp = "^[+]?[0-9]{8,15}$", message = "El teléfono debe tener entre 8 y 15 dígitos")
    @Schema(description = "Número de teléfono (opcional)", example = "+51987654321")
    private String phoneNumber;

    @Pattern(regexp = "^[0-9]{8,12}$", message = "El DNI debe tener entre 8 y 12 dígitos")
    @Schema(description = "Documento Nacional de Identidad (opcional)", example = "12345678")
    private String dni;

    @Size(max = 500, message = "La dirección no puede exceder 500 caracteres")
    @Schema(description = "Dirección del usuario", example = "Av. Principal 123, Lima, Perú")
    private String address;

    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    @Schema(description = "Fecha de nacimiento", example = "1990-05-15")
    private LocalDate birthDate;

    @Schema(description = "Género del usuario", example = "MASCULINO")
    private UserEntity.Gender gender;

    @Schema(description = "Rol del usuario en el sistema", example = "USER")
    private UserEntity.Role role;

    @Size(max = 50, message = "La licencia profesional no puede exceder 50 caracteres")
    @Schema(description = "Número de licencia profesional (para terapeutas)", example = "COP-12345")
    private String professionalLicense;

    @Size(max = 100, message = "La especialización no puede exceder 100 caracteres")
    @Schema(description = "Especialización profesional", example = "Psicología Clínica")
    private String specialization;

    @AssertTrue(message = "Las contraseñas no coinciden")
    @Schema(hidden = true)
    public boolean isPasswordMatching() {
        if (password == null || confirmPassword == null) {
            return false;
        }
        return password.equals(confirmPassword);
    }

    @AssertTrue(message = "Debe ser mayor de 18 años")
    @Schema(hidden = true)
    public boolean isAgeValid() {
        if (birthDate == null) return true;
        return birthDate.isBefore(LocalDate.now().minusYears(18));
    }
}