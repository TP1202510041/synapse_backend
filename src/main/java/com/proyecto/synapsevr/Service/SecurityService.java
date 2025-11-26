package com.proyecto.synapsevr.Service;

import com.proyecto.synapsevr.Entity.UserEntity;

public interface SecurityService {

    /**
     * Registrar intento de login fallido
     */
    void recordFailedLoginAttempt(String email, String ipAddress, String userAgent);

    /**
     * Registrar login exitoso
     */
    void recordSuccessfulLogin(String email, String ipAddress, String userAgent);

    /**
     * Verificar si una cuenta está bloqueada
     */
    boolean isAccountLocked(String email);

    /**
     * Obtener número de intentos fallidos restantes
     */
    int getRemainingAttempts(String email);

    /**
     * Desbloquear cuenta manualmente (solo admin)
     */
    void unlockAccount(String email, String adminEmail);

    /**
     * Validar fortaleza de contraseña
     */
    boolean isPasswordStrong(String password);

    /**
     * Generar mensaje de advertencia por intentos fallidos
     */
    String getSecurityWarningMessage(String email);

    /**
     * Verificar si el usuario necesita cambiar contraseña
     */
    boolean needsPasswordChange(UserEntity user);

    /**
     * Limpiar intentos fallidos antiguos (tarea programada)
     */
    void cleanupOldFailedAttempts();
}
