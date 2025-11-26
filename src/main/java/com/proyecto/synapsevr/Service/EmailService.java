package com.proyecto.synapsevr.Service;

public interface EmailService {
    
    /**
     * Enviar código de verificación para recuperación de contraseña
     */
    void sendPasswordResetCode(String toEmail, String userName, String verificationCode, int validityMinutes);
    
    /**
     * Enviar confirmación de cambio de contraseña
     */
    void sendPasswordChangedConfirmation(String toEmail, String userName);
    
    /**
     * Enviar email de bienvenida
     */
    void sendWelcomeEmail(String toEmail, String userName);
}
