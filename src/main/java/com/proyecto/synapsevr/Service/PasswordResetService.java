package com.proyecto.synapsevr.Service;

import com.proyecto.synapsevr.dto.Request.ForgotPasswordRequest;
import com.proyecto.synapsevr.dto.Request.ResetPasswordRequest;
import com.proyecto.synapsevr.dto.Response.ForgotPasswordResponse;

public interface PasswordResetService {

    /**
     * Solicitar código de recuperación de contraseña
     */
    ForgotPasswordResponse requestPasswordReset(ForgotPasswordRequest request, String ipAddress, String userAgent);

    /**
     * Resetear contraseña con código de verificación
     */
    void resetPassword(ResetPasswordRequest request, String ipAddress);

    /**
     * Verificar si un código es válido
     */
    boolean isValidToken(String email, String token);

    /**
     * Limpiar tokens expirados
     */
    void cleanupExpiredTokens();
}
