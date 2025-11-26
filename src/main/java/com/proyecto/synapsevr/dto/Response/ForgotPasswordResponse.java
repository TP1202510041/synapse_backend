package com.proyecto.synapsevr.dto.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Respuesta de solicitud de recuperación de contraseña")
public class ForgotPasswordResponse {

    @Schema(description = "Email al que se envió el código", example = "usuario@ejemplo.com")
    private String email;

    @Schema(description = "Mensaje de confirmación")
    private String message;

    @Schema(description = "Código de verificación (solo para desarrollo/testing)")
    private String verificationCode;

    @Schema(description = "Tiempo de expiración del código")
    private LocalDateTime expiresAt;

    @Schema(description = "Minutos de validez del código", example = "15")
    private Integer validityMinutes;

    @Schema(description = "Indica si se puede solicitar otro código", example = "false")
    private Boolean canRequestAnother;

    @Schema(description = "Segundos hasta poder solicitar otro código", example = "120")
    private Long secondsUntilNextRequest;

    public static ForgotPasswordResponse success(String email, String code, LocalDateTime expiresAt, boolean canRequestAnother, Long secondsUntilNext) {
        ForgotPasswordResponse response = new ForgotPasswordResponse();
        response.setEmail(email);
        response.setMessage("Código de verificación enviado exitosamente. Revisa tu email.");
        response.setVerificationCode(code); // En producción, esto NO se debe enviar
        response.setExpiresAt(expiresAt);
        response.setValidityMinutes(15);
        response.setCanRequestAnother(canRequestAnother);
        response.setSecondsUntilNextRequest(secondsUntilNext);
        return response;
    }
}
