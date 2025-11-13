package com.proyecto.synapsevr.Service.ServiceImpl;

import com.proyecto.synapsevr.Service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.from:noreply@synapsevr.com}")
    private String fromEmail;

    @Value("${app.email.from-name:SynapseVR Security}")
    private String fromName;

    @Override
    public void sendPasswordResetCode(String toEmail, String userName, String verificationCode, int validityMinutes) {
        try {
            // Mostrar en consola también
            printEmailToConsole("Código de Recuperación", toEmail, userName, verificationCode, validityMinutes);
            
            // Enviar email real
            String subject = "🔐 Código de Recuperación de Contraseña - SynapseVR";
            String htmlContent = buildPasswordResetEmail(userName, verificationCode, validityMinutes);
            
            sendHtmlEmail(toEmail, subject, htmlContent);
            
            System.out.println("✅ [EMAIL] Código enviado exitosamente a: " + toEmail + "\n");
            
        } catch (Exception e) {
            System.err.println("❌ [EMAIL] Error al enviar email a " + toEmail + ": " + e.getMessage());
            System.err.println("⚠️ El código sigue siendo válido, pero el email no se pudo enviar.");
            System.err.println("💡 Verifica la configuración de email en application.properties\n");
        }
    }

    @Override
    public void sendPasswordChangedConfirmation(String toEmail, String userName) {
        try {
            System.out.println("\n" + "=".repeat(80));
            System.out.println("✅ [EMAIL] Enviando confirmación de cambio de contraseña a: " + toEmail);
            System.out.println("=".repeat(80) + "\n");
            
            String subject = "✅ Contraseña Actualizada - SynapseVR";
            String htmlContent = buildPasswordChangedEmail(userName);
            
            sendHtmlEmail(toEmail, subject, htmlContent);
            
            System.out.println("✅ [EMAIL] Confirmación enviada exitosamente\n");
            
        } catch (Exception e) {
            System.err.println("❌ [EMAIL] Error al enviar confirmación: " + e.getMessage() + "\n");
        }
    }

    @Override
    public void sendWelcomeEmail(String toEmail, String userName) {
        try {
            System.out.println("\n" + "=".repeat(80));
            System.out.println("🎉 [EMAIL] Enviando email de bienvenida a: " + toEmail);
            System.out.println("=".repeat(80) + "\n");
            
            String subject = "🎉 Bienvenido a SynapseVR";
            String htmlContent = buildWelcomeEmail(userName);
            
            sendHtmlEmail(toEmail, subject, htmlContent);
            
            System.out.println("✅ [EMAIL] Bienvenida enviada exitosamente\n");
            
        } catch (Exception e) {
            System.err.println("❌ [EMAIL] Error al enviar bienvenida: " + e.getMessage() + "\n");
        }
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
        } catch (jakarta.mail.MessagingException e) {
            throw new MessagingException("Error al enviar email", e);
        } catch (java.io.UnsupportedEncodingException e) {
            throw new MessagingException("Error de codificación al enviar email", e);
        }
    }

    private void printEmailToConsole(String tipo, String toEmail, String userName, String code, int validityMinutes) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("📧 [EMAIL] " + tipo);
        System.out.println("=".repeat(80));
        System.out.println("Para: " + toEmail);
        System.out.println("Usuario: " + userName);
        System.out.println("-".repeat(80));
        System.out.println("\n  Tu código de verificación es:\n");
        System.out.println("  ┌─────────────────────────────┐");
        System.out.println("  │                             │");
        System.out.println("  │      " + code + "      │");
        System.out.println("  │                             │");
        System.out.println("  │   Válido por " + validityMinutes + " minutos   │");
        System.out.println("  │                             │");
        System.out.println("  └─────────────────────────────┘\n");
        System.out.println("-".repeat(80));
        System.out.println("⏰ Expira: " + LocalDateTime.now().plusMinutes(validityMinutes)
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        System.out.println("=".repeat(80));
    }

    private String buildPasswordResetEmail(String userName, String code, int validityMinutes) {
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; background: #f4f4f4; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 20px auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 0 20px rgba(0,0,0,0.1); }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 40px 20px; text-align: center; }
                    .header h1 { margin: 0; font-size: 28px; }
                    .content { padding: 40px 30px; }
                    .code-box { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; margin: 30px 0; border-radius: 10px; box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4); }
                    .code { font-size: 48px; font-weight: bold; letter-spacing: 10px; font-family: 'Courier New', monospace; margin: 20px 0; }
                    .validity { font-size: 16px; opacity: 0.9; }
                    .warning { background: #fff3cd; border-left: 4px solid #ffc107; padding: 20px; margin: 25px 0; border-radius: 5px; }
                    .info-box { background: #f8f9fa; padding: 20px; border-radius: 5px; margin: 20px 0; }
                    .footer { background: #f8f9fa; padding: 30px; text-align: center; color: #666; font-size: 14px; }
                    .instructions { background: #e7f3ff; padding: 20px; border-radius: 5px; margin: 20px 0; }
                    .instructions ol { margin: 10px 0; padding-left: 20px; }
                    .instructions li { margin: 8px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🔐 Recuperación de Contraseña</h1>
                        <p style="margin: 10px 0 0 0; font-size: 18px;">SynapseVR Security</p>
                    </div>
                    <div class="content">
                        <p style="font-size: 16px;">Hola <strong>%s</strong>,</p>
                        
                        <p>Recibimos una solicitud para restablecer la contraseña de tu cuenta en SynapseVR.</p>
                        
                        <div class="code-box">
                            <p style="margin: 0; font-size: 14px; opacity: 0.9;">Tu código de verificación es:</p>
                            <div class="code">%s</div>
                            <p class="validity">Válido por %d minutos</p>
                        </div>
                        
                        <div class="instructions">
                            <strong>📋 Instrucciones:</strong>
                            <ol>
                                <li>Ingresa este código en la pantalla de recuperación</li>
                                <li>Crea una nueva contraseña segura</li>
                                <li>Tu cuenta será desbloqueada automáticamente</li>
                            </ol>
                        </div>
                        
                        <div class="warning">
                            <strong>⚠️ Importante:</strong>
                            <ul style="margin: 10px 0 0 0; padding-left: 20px;">
                                <li>Este código expira en <strong>%d minutos</strong></li>
                                <li>Solo puedes usar este código <strong>3 veces</strong></li>
                                <li>Si no solicitaste este código, ignora este email</li>
                            </ul>
                        </div>
                        
                        <div class="info-box">
                            <strong>📊 Detalles de la solicitud:</strong>
                            <ul style="margin: 10px 0 0 0; padding-left: 20px;">
                                <li>Fecha y hora: %s</li>
                                <li>Email: %s</li>
                            </ul>
                        </div>
                        
                        <p style="margin-top: 30px; color: #666;">Si tienes problemas, contacta a nuestro equipo de soporte.</p>
                    </div>
                    <div class="footer">
                        <p style="margin: 5px 0;">Este es un email automático, por favor no respondas.</p>
                        <p style="margin: 5px 0;">&copy; 2025 SynapseVR. Todos los derechos reservados.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(userName, code, validityMinutes, validityMinutes, currentTime, userName);
    }

    private String buildPasswordChangedEmail(String userName) {
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; background: #f4f4f4; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 20px auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 0 20px rgba(0,0,0,0.1); }
                    .header { background: linear-gradient(135deg, #11998e 0%%, #38ef7d 100%%); color: white; padding: 40px 20px; text-align: center; }
                    .header h1 { margin: 0; font-size: 28px; }
                    .content { padding: 40px 30px; }
                    .success-box { background: #d4edda; border-left: 4px solid #28a745; padding: 20px; margin: 25px 0; border-radius: 5px; }
                    .info-box { background: #f8f9fa; padding: 20px; border-radius: 5px; margin: 20px 0; }
                    .warning-box { background: #fff3cd; border-left: 4px solid #ffc107; padding: 20px; margin: 25px 0; border-radius: 5px; }
                    .footer { background: #f8f9fa; padding: 30px; text-align: center; color: #666; font-size: 14px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>✅ Contraseña Actualizada</h1>
                        <p style="margin: 10px 0 0 0; font-size: 18px;">SynapseVR Security</p>
                    </div>
                    <div class="content">
                        <p style="font-size: 16px;">Hola <strong>%s</strong>,</p>
                        
                        <div class="success-box">
                            <strong style="font-size: 18px;">✅ Tu contraseña ha sido actualizada exitosamente</strong>
                        </div>
                        
                        <p>Tu cuenta ha sido desbloqueada y ya puedes iniciar sesión con tu nueva contraseña.</p>
                        
                        <div class="info-box">
                            <strong>📊 Detalles del cambio:</strong>
                            <ul style="margin: 10px 0 0 0; padding-left: 20px;">
                                <li>Fecha y hora: %s</li>
                                <li>Cuenta: %s</li>
                            </ul>
                        </div>
                        
                        <div class="warning-box">
                            <strong>⚠️ ¿No fuiste tú?</strong>
                            <p style="margin: 10px 0 0 0;">Si no realizaste este cambio, contacta inmediatamente a nuestro equipo de soporte.</p>
                        </div>
                    </div>
                    <div class="footer">
                        <p style="margin: 5px 0;">Este es un email automático, por favor no respondas.</p>
                        <p style="margin: 5px 0;">&copy; 2025 SynapseVR. Todos los derechos reservados.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(userName, currentTime, userName);
    }

    private String buildWelcomeEmail(String userName) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; background: #f4f4f4; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 20px auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 0 20px rgba(0,0,0,0.1); }
                    .header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 40px 20px; text-align: center; }
                    .header h1 { margin: 0; font-size: 28px; }
                    .content { padding: 40px 30px; }
                    .footer { background: #f8f9fa; padding: 30px; text-align: center; color: #666; font-size: 14px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎉 ¡Bienvenido a SynapseVR!</h1>
                    </div>
                    <div class="content">
                        <p style="font-size: 16px;">Hola <strong>%s</strong>,</p>
                        
                        <p>¡Gracias por registrarte en SynapseVR! Estamos emocionados de tenerte con nosotros.</p>
                        
                        <p>Tu cuenta ha sido creada exitosamente y ya puedes comenzar a usar nuestra plataforma de realidad virtual para terapia.</p>
                        
                        <p style="margin-top: 30px;">Si tienes alguna pregunta, no dudes en contactarnos.</p>
                    </div>
                    <div class="footer">
                        <p style="margin: 5px 0;">&copy; 2025 SynapseVR. Todos los derechos reservados.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(userName);
    }
}
