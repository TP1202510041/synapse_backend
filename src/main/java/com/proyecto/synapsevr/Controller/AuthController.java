package com.proyecto.synapsevr.Controller;

import com.proyecto.synapsevr.dto.Response.ApiResponse;
import com.proyecto.synapsevr.dto.Request.LoginRequest;
import com.proyecto.synapsevr.dto.Request.RegisterRequest;
import com.proyecto.synapsevr.dto.Request.ForgotPasswordRequest;
import com.proyecto.synapsevr.dto.Request.ResetPasswordRequest;
import com.proyecto.synapsevr.dto.Response.AuthResponse;
import com.proyecto.synapsevr.dto.Response.ForgotPasswordResponse;
import com.proyecto.synapsevr.Service.AuthService;
import com.proyecto.synapsevr.Service.SecurityService;
import com.proyecto.synapsevr.Service.PasswordResetService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Authentication", description = "API para autenticación de usuarios")
public class AuthController {

    private final AuthService authService;
    private final SecurityService securityService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario con validaciones completas", description = "Registra un nuevo usuario en el sistema con validaciones de seguridad")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de registro inválidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest) {
        try {
            System.out.println("🔐 [AUTH] Intento de registro para: " + request.getEmail());
            
            // Validaciones adicionales de seguridad
            if (!securityService.isPasswordStrong(request.getPassword())) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("La contraseña no cumple con los requisitos de seguridad"));
            }
            
            String ipAddress = getClientIpAddress(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");
            
            AuthResponse authResponse = authService.registerUser(request);
            
            System.out.println("✅ Usuario registrado exitosamente: " + request.getEmail());
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Usuario registrado exitosamente. Verifica tu email para activar la cuenta.", authResponse));
        } catch (RuntimeException e) {
            System.err.println("❌ Error en registro: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            System.err.println("❌ Error interno en registro: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor"));
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión con validaciones de seguridad", description = "Autentica un usuario y devuelve un token JWT con control de intentos fallidos")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login exitoso"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "423", description = "Cuenta bloqueada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        
        String ipAddress = getClientIpAddress(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        
        try {
            System.out.println("🔐 [AUTH] Intento de login para: " + request.getEmail());
            System.out.println("🌐 IP: " + ipAddress + ", User-Agent: " + (userAgent != null ? userAgent.substring(0, Math.min(50, userAgent.length())) : "N/A"));
            
            // Verificar si la cuenta está bloqueada
            if (securityService.isAccountLocked(request.getEmail())) {
                String warningMessage = securityService.getSecurityWarningMessage(request.getEmail());
                System.out.println("🚫 Intento de login en cuenta bloqueada: " + request.getEmail());
                return ResponseEntity.status(HttpStatus.LOCKED)
                        .body(ApiResponse.error(warningMessage != null ? warningMessage : "Cuenta bloqueada temporalmente"));
            }
            
            AuthResponse authResponse = authService.loginUser(request);
            
            // Registrar login exitoso
            securityService.recordSuccessfulLogin(request.getEmail(), ipAddress, userAgent);
            
            System.out.println("✅ Login exitoso para: " + request.getEmail());
            return ResponseEntity.ok(ApiResponse.success("Login exitoso", authResponse));
            
        } catch (RuntimeException e) {
            System.err.println("❌ Login fallido para: " + request.getEmail() + " - " + e.getMessage());
            
            // Registrar intento fallido
            securityService.recordFailedLoginAttempt(request.getEmail(), ipAddress, userAgent);
            
            // Obtener mensaje de advertencia personalizado
            String warningMessage = securityService.getSecurityWarningMessage(request.getEmail());
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(warningMessage != null ? warningMessage : "Credenciales inválidas"));
        } catch (Exception e) {
            System.err.println("❌ Error interno en login: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor"));
        }
    }

    @GetMapping("/check-email")
    @Operation(summary = "Verificar email", description = "Verifica si un email ya existe en el sistema")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Email verificado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error al verificar email")
    })
    public ResponseEntity<ApiResponse<Boolean>> checkEmail(
            @Parameter(description = "Email a verificar") @RequestParam String email) {
        try {
            boolean exists = authService.emailExists(email);

            return ResponseEntity.ok(
                    ApiResponse.success("Email verificado", exists)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al verificar email"));
        }
    }

    @GetMapping("/test")
    @Operation(summary = "Test endpoint", description = "Endpoint de prueba para verificar que la API funciona")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "API funcionando correctamente")
    public ResponseEntity<ApiResponse<String>> test() {
        return ResponseEntity.ok(
                ApiResponse.success("API de autenticación funcionando correctamente")
        );
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Solicitar código de recuperación de contraseña", 
               description = "Envía un código de 6 dígitos al email del usuario para recuperar su contraseña. Incluye protección contra abuso.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Código enviado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "429", description = "Demasiadas solicitudes"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ApiResponse<ForgotPasswordResponse>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request,
            HttpServletRequest httpRequest) {
        try {
            String ipAddress = getClientIpAddress(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");
            
            System.out.println("🔐 [FORGOT PASSWORD] Solicitud para: " + request.getEmail());
            
            ForgotPasswordResponse response = passwordResetService.requestPasswordReset(request, ipAddress, userAgent);
            
            return ResponseEntity.ok(
                    ApiResponse.success("Código de verificación enviado. Revisa tu email.", response)
            );
        } catch (RuntimeException e) {
            System.err.println("❌ Error en forgot-password: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            System.err.println("❌ Error interno en forgot-password: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor"));
        }
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Restablecer contraseña con código de verificación", 
               description = "Permite cambiar la contraseña usando el código de 6 dígitos recibido por email. También desbloquea la cuenta si estaba bloqueada.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Contraseña restablecida exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Código inválido o expirado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request,
            HttpServletRequest httpRequest) {
        try {
            String ipAddress = getClientIpAddress(httpRequest);
            
            System.out.println("🔐 [RESET PASSWORD] Intento para: " + request.getEmail());
            
            passwordResetService.resetPassword(request, ipAddress);
            
            System.out.println("✅ Contraseña restablecida y cuenta desbloqueada para: " + request.getEmail());
            
            return ResponseEntity.ok(
                    ApiResponse.success("✅ Contraseña restablecida exitosamente. Tu cuenta ha sido desbloqueada y ya puedes iniciar sesión.", null)
            );
        } catch (RuntimeException e) {
            System.err.println("❌ Error en reset-password: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            System.err.println("❌ Error interno en reset-password: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor"));
        }
    }

    @PostMapping("/verify-reset-code")
    @Operation(summary = "Verificar código de recuperación", 
               description = "Verifica si un código de recuperación es válido sin usarlo")
    public ResponseEntity<ApiResponse<Boolean>> verifyResetCode(
            @RequestParam String email,
            @RequestParam String code) {
        try {
            boolean isValid = passwordResetService.isValidToken(email, code);
            
            if (isValid) {
                return ResponseEntity.ok(
                        ApiResponse.success("Código válido", true)
                );
            } else {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Código inválido o expirado"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor"));
        }
    }

    @GetMapping("/login-attempts/{userId}")
    @Operation(summary = "Consultar intentos de login (admin)")
    public ResponseEntity<ApiResponse<List<Object>>> getLoginAttempts(
            @PathVariable Integer userId) {
        try {
            List<Object> attempts = authService.getLoginAttempts(userId);
            return ResponseEntity.ok(
                    ApiResponse.success("Intentos de login obtenidos", attempts)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor"));
        }
    }

    private String getClientIpAddress(jakarta.servlet.http.HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }
}