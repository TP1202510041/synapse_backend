package com.proyecto.synapsevr.Service.ServiceImpl;

import com.proyecto.synapsevr.Entity.UserEntity;
import com.proyecto.synapsevr.Entity.LoginAttemptEntity;
import com.proyecto.synapsevr.Repository.UserRepository;
import com.proyecto.synapsevr.Repository.LoginAttemptRepository;
import com.proyecto.synapsevr.Service.SecurityService;
import com.proyecto.synapsevr.Service.AuditService;
import com.proyecto.synapsevr.Entity.AuditLogEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final UserRepository userRepository;
    private final LoginAttemptRepository loginAttemptRepository;
    private final AuditService auditService;

    // Patrón para validar contraseña fuerte
    private static final Pattern STRONG_PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_HOURS = 1;

    @Override
    @Transactional
    public void recordFailedLoginAttempt(String email, String ipAddress, String userAgent) {
        System.out.println("🔒 [SECURITY] Registrando intento fallido para: " + email);
        
        // Registrar en login_attempts
        LoginAttemptEntity attempt = new LoginAttemptEntity();
        attempt.setEmail(email);
        attempt.setIpAddress(ipAddress);
        attempt.setUserAgent(userAgent);
        attempt.setSuccessful(false);
        attempt.setAttemptTime(LocalDateTime.now());
        loginAttemptRepository.save(attempt);

        // Actualizar contador en usuario
        userRepository.findByEmail(email).ifPresent(user -> {
            user.incrementFailedAttempts();
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            System.out.println("⚠️ Intentos fallidos: " + user.getFailedLoginAttempts() + "/" + MAX_FAILED_ATTEMPTS);
            
            if (user.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
                System.out.println("🚫 Cuenta bloqueada por " + LOCKOUT_DURATION_HOURS + " hora(s)");
                
                // Auditoría de bloqueo
                auditService.logAction(user.getUserId(), "User", String.valueOf(user.getUserId()),
                        "account_status", "ACTIVE", "LOCKED", AuditLogEntity.AuditAction.UPDATE, 
                        ipAddress, userAgent);
            }
        });
    }

    @Override
    @Transactional
    public void recordSuccessfulLogin(String email, String ipAddress, String userAgent) {
        System.out.println("✅ [SECURITY] Login exitoso para: " + email);
        
        // Registrar en login_attempts
        LoginAttemptEntity attempt = new LoginAttemptEntity();
        attempt.setEmail(email);
        attempt.setIpAddress(ipAddress);
        attempt.setUserAgent(userAgent);
        attempt.setSuccessful(true);
        attempt.setAttemptTime(LocalDateTime.now());
        loginAttemptRepository.save(attempt);

        // Resetear contador y actualizar último login
        userRepository.findByEmail(email).ifPresent(user -> {
            user.resetFailedAttempts();
            user.setLastLogin(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            System.out.println("🔓 Intentos fallidos reseteados para: " + email);
        });
    }

    @Override
    public boolean isAccountLocked(String email) {
        return userRepository.findByEmail(email)
                .map(UserEntity::isAccountLocked)
                .orElse(false);
    }

    @Override
    public int getRemainingAttempts(String email) {
        return userRepository.findByEmail(email)
                .map(user -> Math.max(0, MAX_FAILED_ATTEMPTS - (user.getFailedLoginAttempts() != null ? user.getFailedLoginAttempts() : 0)))
                .orElse(MAX_FAILED_ATTEMPTS);
    }

    @Override
    @Transactional
    public void unlockAccount(String email, String adminEmail) {
        UserEntity admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado"));
        
        if (admin.getRole() != UserEntity.Role.ADMIN) {
            throw new RuntimeException("Solo los administradores pueden desbloquear cuentas");
        }

        userRepository.findByEmail(email).ifPresent(user -> {
            user.setAccountStatus(UserEntity.AccountStatus.ACTIVE);
            user.setFailedLoginAttempts(0);
            user.setAccountLockedUntil(null);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            System.out.println("🔓 Cuenta desbloqueada por admin: " + email);
            
            // Auditoría
            auditService.logAction(admin.getUserId(), "User", String.valueOf(user.getUserId()),
                    "account_status", "LOCKED", "ACTIVE", AuditLogEntity.AuditAction.UPDATE, null, null);
        });
    }

    @Override
    public boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        return STRONG_PASSWORD_PATTERN.matcher(password).matches();
    }

    @Override
    public String getSecurityWarningMessage(String email) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    int attempts = user.getFailedLoginAttempts() != null ? user.getFailedLoginAttempts() : 0;
                    int remaining = MAX_FAILED_ATTEMPTS - attempts;
                    
                    if (attempts == 0) {
                        return null;
                    } else if (attempts < 3) {
                        return String.format("⚠️ Credenciales incorrectas. Te quedan %d intentos antes del bloqueo.", remaining);
                    } else if (attempts < MAX_FAILED_ATTEMPTS) {
                        return String.format("🚨 ADVERTENCIA: Solo te quedan %d intentos. Tu cuenta se bloqueará por %d hora(s) si fallas nuevamente.", 
                                           remaining, LOCKOUT_DURATION_HOURS);
                    } else {
                        LocalDateTime unlockTime = user.getAccountLockedUntil();
                        if (unlockTime != null) {
                            return String.format("🔒 Cuenta bloqueada hasta: %s. Contacta al administrador si necesitas ayuda.", 
                                               unlockTime.toString());
                        } else {
                            return "🔒 Cuenta bloqueada. Contacta al administrador.";
                        }
                    }
                })
                .orElse(null);
    }

    @Override
    public boolean needsPasswordChange(UserEntity user) {
        return user.needsPasswordChange();
    }

    @Override
    @Transactional
    public void cleanupOldFailedAttempts() {
        System.out.println("🧹 [SECURITY] Limpiando intentos de login antiguos...");
        
        // Limpiar intentos de más de 30 días
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        loginAttemptRepository.deleteByAttemptTimeBefore(cutoffDate);
        
        // Desbloquear cuentas que ya cumplieron su tiempo
        LocalDateTime now = LocalDateTime.now();
        userRepository.findByAccountStatusAndAccountLockedUntilBefore(
                UserEntity.AccountStatus.LOCKED, now
        ).forEach(user -> {
            user.setAccountStatus(UserEntity.AccountStatus.ACTIVE);
            user.setAccountLockedUntil(null);
            user.setFailedLoginAttempts(0);
            user.setUpdatedAt(now);
            userRepository.save(user);
            
            System.out.println("🔓 Auto-desbloqueada cuenta: " + user.getEmail());
        });
    }
}
