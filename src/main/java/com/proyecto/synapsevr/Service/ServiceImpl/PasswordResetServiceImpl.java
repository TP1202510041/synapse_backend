package com.proyecto.synapsevr.Service.ServiceImpl;

import com.proyecto.synapsevr.Entity.PasswordResetTokenEntity;
import com.proyecto.synapsevr.Entity.UserEntity;
import com.proyecto.synapsevr.Repository.PasswordResetTokenRepository;
import com.proyecto.synapsevr.Repository.UserRepository;
import com.proyecto.synapsevr.Service.PasswordResetService;
import com.proyecto.synapsevr.Service.EmailService;
import com.proyecto.synapsevr.dto.Request.ForgotPasswordRequest;
import com.proyecto.synapsevr.dto.Request.ResetPasswordRequest;
import com.proyecto.synapsevr.dto.Response.ForgotPasswordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private static final int TOKEN_VALIDITY_MINUTES = 15;
    private static final int MAX_REQUESTS_PER_HOUR = 3;
    private static final int COOLDOWN_SECONDS = 120; // 2 minutos entre solicitudes
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    @Transactional
    public ForgotPasswordResponse requestPasswordReset(ForgotPasswordRequest request, String ipAddress, String userAgent) {
        String email = request.getEmail().toLowerCase().trim();
        
        System.out.println("🔐 [PASSWORD RESET] Solicitud de recuperación para: " + email);
        
        // Verificar que el usuario existe (case-insensitive)
        Optional<UserEntity> userOpt = userRepository.findByEmailIgnoreCase(email);
        if (userOpt.isEmpty()) {
            // Por seguridad, no revelamos si el email existe o no
            System.out.println("⚠️ Email no encontrado, pero respondemos genéricamente por seguridad");
            return createGenericResponse(email);
        }

        UserEntity user = userOpt.get();
        
        // Verificar límite de solicitudes por hora
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        long recentRequests = tokenRepository.countByEmailAndCreatedAtAfter(email, oneHourAgo);
        
        if (recentRequests >= MAX_REQUESTS_PER_HOUR) {
            throw new RuntimeException("Has excedido el límite de solicitudes. Intenta nuevamente en 1 hora.");
        }

        // Verificar cooldown entre solicitudes
        LocalDateTime cooldownTime = LocalDateTime.now().minusSeconds(COOLDOWN_SECONDS);
        Optional<PasswordResetTokenEntity> recentToken = tokenRepository.findByEmailAndUsedFalseOrderByCreatedAtDesc(email);
        
        if (recentToken.isPresent() && recentToken.get().getCreatedAt().isAfter(cooldownTime)) {
            long secondsUntilNext = ChronoUnit.SECONDS.between(LocalDateTime.now(), recentToken.get().getCreatedAt().plusSeconds(COOLDOWN_SECONDS));
            throw new RuntimeException("Debes esperar " + secondsUntilNext + " segundos antes de solicitar otro código.");
        }

        // Generar código de 6 dígitos
        String verificationCode = generateVerificationCode();
        
        // Crear token
        PasswordResetTokenEntity token = new PasswordResetTokenEntity();
        token.setEmail(email);
        token.setToken(verificationCode);
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusMinutes(TOKEN_VALIDITY_MINUTES));
        token.setUsed(false);
        token.setIpAddress(ipAddress);
        token.setUserAgent(userAgent);
        token.setAttempts(0);
        
        tokenRepository.save(token);
        
        // Mostrar código en consola (simulación de email)
        emailService.sendPasswordResetCode(email, user.getUserName(), verificationCode, TOKEN_VALIDITY_MINUTES);
        
        // Si la cuenta estaba bloqueada, informar que se puede desbloquear
        if (user.isAccountLocked()) {
            System.out.println("🔓 La cuenta está bloqueada. El código permitirá desbloquearla al cambiar la contraseña.");
        }
        
        // En desarrollo, incluir el código en la respuesta
        // En producción, cambiar verificationCode por null
        return ForgotPasswordResponse.success(
            email, 
            verificationCode, // TODO: Cambiar a null en producción
            token.getExpiresAt(),
            false,
            (long) COOLDOWN_SECONDS
        );
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request, String ipAddress) {
        String email = request.getEmail().toLowerCase().trim();
        
        System.out.println("🔐 [PASSWORD RESET] Intentando resetear contraseña para: " + email);
        
        // Validar que las contraseñas coincidan
        if (!request.isPasswordMatching()) {
            throw new RuntimeException("Las contraseñas no coinciden");
        }

        // Buscar usuario (case-insensitive)
        UserEntity user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Buscar token válido
        PasswordResetTokenEntity token = tokenRepository.findByTokenAndUsedFalse(request.getVerificationCode())
                .orElseThrow(() -> new RuntimeException("Código de verificación inválido o ya usado"));

        // Verificar que el token pertenece al email correcto
        if (!token.getEmail().equalsIgnoreCase(email)) {
            System.err.println("⚠️ Intento de usar código de otro usuario");
            throw new RuntimeException("Código de verificación inválido");
        }

        // Incrementar intentos
        token.setAttempts(token.getAttempts() + 1);
        tokenRepository.save(token);

        // Verificar validez del token
        if (!token.isValid()) {
            if (token.isExpired()) {
                throw new RuntimeException("El código de verificación ha expirado. Solicita uno nuevo.");
            }
            if (token.getAttempts() >= 3) {
                throw new RuntimeException("Has excedido el número de intentos. Solicita un nuevo código.");
            }
            throw new RuntimeException("Código de verificación inválido");
        }

        // Validar fortaleza de la nueva contraseña
        if (!isStrongPassword(request.getNewPassword())) {
            throw new RuntimeException("La contraseña no cumple con los requisitos de seguridad");
        }

        // Actualizar contraseña
        user.setUserPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordChangedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        // IMPORTANTE: Desbloquear cuenta y resetear intentos fallidos
        user.setAccountStatus(UserEntity.AccountStatus.ACTIVE);
        user.setAccountLockedUntil(null);
        user.setFailedLoginAttempts(0);
        
        userRepository.save(user);

        // Marcar token como usado
        token.setUsed(true);
        token.setUsedAt(LocalDateTime.now());
        tokenRepository.save(token);

        System.out.println("✅ Contraseña actualizada exitosamente para: " + email);
        System.out.println("🔓 Cuenta desbloqueada y lista para usar");
        
        // Mostrar confirmación en consola (simulación de email)
        emailService.sendPasswordChangedConfirmation(email, user.getUserName());
    }

    @Override
    public boolean isValidToken(String email, String tokenValue) {
        Optional<PasswordResetTokenEntity> tokenOpt = tokenRepository.findByTokenAndUsedFalse(tokenValue);
        
        if (tokenOpt.isEmpty()) {
            return false;
        }

        PasswordResetTokenEntity token = tokenOpt.get();
        return token.getEmail().equalsIgnoreCase(email) && token.isValid();
    }

    @Override
    @Transactional
    public void cleanupExpiredTokens() {
        System.out.println("🧹 [PASSWORD RESET] Limpiando tokens expirados...");
        tokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
        System.out.println("✅ Limpieza de tokens completada");
    }

    private String generateVerificationCode() {
        // Generar código de 6 dígitos
        int code = 100000 + RANDOM.nextInt(900000);
        return String.valueOf(code);
    }

    private ForgotPasswordResponse createGenericResponse(String email) {
        // Respuesta genérica para no revelar si el email existe
        return ForgotPasswordResponse.success(
            email,
            "000000", // Código falso
            LocalDateTime.now().plusMinutes(TOKEN_VALIDITY_MINUTES),
            false,
            (long) COOLDOWN_SECONDS
        );
    }

    private boolean isStrongPassword(String password) {
        if (password.length() < 8) return false;
        if (!password.matches(".*[a-z].*")) return false;
        if (!password.matches(".*[A-Z].*")) return false;
        if (!password.matches(".*\\d.*")) return false;
        if (!password.matches(".*[@$!%*?&].*")) return false;
        
        String[] commonPasswords = {"password", "123456", "qwerty", "admin", "letmein"};
        String lowerPassword = password.toLowerCase();
        for (String common : commonPasswords) {
            if (lowerPassword.contains(common)) {
                return false;
            }
        }
        
        return true;
    }
}
