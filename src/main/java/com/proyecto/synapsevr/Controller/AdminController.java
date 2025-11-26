package com.proyecto.synapsevr.Controller;

import com.proyecto.synapsevr.Entity.UserEntity;
import com.proyecto.synapsevr.Entity.LoginAttemptEntity;
import com.proyecto.synapsevr.Repository.UserRepository;
import com.proyecto.synapsevr.Repository.LoginAttemptRepository;
import com.proyecto.synapsevr.dto.Response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Admin", description = "API de administración (solo para debugging)")
public class AdminController {

    private final UserRepository userRepository;
    private final LoginAttemptRepository loginAttemptRepository;

    @GetMapping("/users")
    @Operation(summary = "Ver todas las cuentas registradas (SOLO PARA DEBUGGING)")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllUsers() {
        try {
            List<UserEntity> users = userRepository.findAll();
            
            List<Map<String, Object>> userList = users.stream().map(user -> {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("userId", user.getUserId());
                userMap.put("email", user.getEmail());
                userMap.put("userName", user.getUserName());
                userMap.put("role", user.getRole());
                userMap.put("phoneNumber", user.getPhoneNumber());
                userMap.put("dni", user.getDni());
                userMap.put("accountStatus", user.getAccountStatus());
                userMap.put("emailVerified", user.getEmailVerified());
                userMap.put("failedLoginAttempts", user.getFailedLoginAttempts());
                userMap.put("lastLogin", user.getLastLogin());
                userMap.put("createdAt", user.getCreatedAt());
                userMap.put("passwordHash", user.getUserPassword().substring(0, 20) + "..."); // Solo primeros 20 chars
                return userMap;
            }).collect(Collectors.toList());
            
            System.out.println("📊 Total de usuarios en BD: " + users.size());
            
            return ResponseEntity.ok(
                ApiResponse.success("Usuarios obtenidos exitosamente", userList)
            );
        } catch (Exception e) {
            System.err.println("❌ Error al obtener usuarios: " + e.getMessage());
            return ResponseEntity.status(500)
                .body(ApiResponse.error("Error al obtener usuarios: " + e.getMessage()));
        }
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "Ver detalles de un usuario específico")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserById(@PathVariable Integer userId) {
        try {
            UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("userId", user.getUserId());
            userDetails.put("email", user.getEmail());
            userDetails.put("userName", user.getUserName());
            userDetails.put("role", user.getRole());
            userDetails.put("phoneNumber", user.getPhoneNumber());
            userDetails.put("dni", user.getDni());
            userDetails.put("address", user.getAddress());
            userDetails.put("birthDate", user.getBirthDate());
            userDetails.put("gender", user.getGender());
            userDetails.put("professionalLicense", user.getProfessionalLicense());
            userDetails.put("specialization", user.getSpecialization());
            userDetails.put("accountStatus", user.getAccountStatus());
            userDetails.put("emailVerified", user.getEmailVerified());
            userDetails.put("failedLoginAttempts", user.getFailedLoginAttempts());
            userDetails.put("accountLockedUntil", user.getAccountLockedUntil());
            userDetails.put("lastLogin", user.getLastLogin());
            userDetails.put("passwordChangedAt", user.getPasswordChangedAt());
            userDetails.put("createdAt", user.getCreatedAt());
            userDetails.put("updatedAt", user.getUpdatedAt());
            userDetails.put("passwordHash", user.getUserPassword().substring(0, 30) + "...");
            
            return ResponseEntity.ok(
                ApiResponse.success("Usuario obtenido exitosamente", userDetails)
            );
        } catch (Exception e) {
            return ResponseEntity.status(404)
                .body(ApiResponse.error("Usuario no encontrado: " + e.getMessage()));
        }
    }

    @GetMapping("/login-attempts")
    @Operation(summary = "Ver todos los intentos de login")
    public ResponseEntity<ApiResponse<List<LoginAttemptEntity>>> getAllLoginAttempts() {
        try {
            List<LoginAttemptEntity> attempts = loginAttemptRepository.findAll();
            
            System.out.println("📊 Total de intentos de login: " + attempts.size());
            
            return ResponseEntity.ok(
                ApiResponse.success("Intentos de login obtenidos", attempts)
            );
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error("Error al obtener intentos: " + e.getMessage()));
        }
    }

    @GetMapping("/login-attempts/{email}")
    @Operation(summary = "Ver intentos de login de un email específico")
    public ResponseEntity<ApiResponse<List<LoginAttemptEntity>>> getLoginAttemptsByEmail(
            @PathVariable String email) {
        try {
            List<LoginAttemptEntity> attempts = loginAttemptRepository.findByEmailOrderByAttemptTimeDesc(email);
            
            return ResponseEntity.ok(
                ApiResponse.success("Intentos de login para " + email, attempts)
            );
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error("Error al obtener intentos: " + e.getMessage()));
        }
    }

    @GetMapping("/database-info")
    @Operation(summary = "Ver información de la base de datos")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDatabaseInfo() {
        try {
            Map<String, Object> info = new HashMap<>();
            
            long totalUsers = userRepository.count();
            long totalAttempts = loginAttemptRepository.count();
            
            info.put("totalUsers", totalUsers);
            info.put("totalLoginAttempts", totalAttempts);
            info.put("databaseUrl", "jdbc:h2:file:./data/synapsevr");
            info.put("h2ConsoleUrl", "http://localhost:5000/h2-console");
            info.put("h2ConsoleUsername", "sa");
            info.put("h2ConsolePassword", "(empty)");
            
            System.out.println("📊 INFO BD - Usuarios: " + totalUsers + ", Intentos: " + totalAttempts);
            
            return ResponseEntity.ok(
                ApiResponse.success("Información de la base de datos", info)
            );
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error("Error al obtener info: " + e.getMessage()));
        }
    }

    @DeleteMapping("/users/{userId}")
    @Operation(summary = "Eliminar un usuario (CUIDADO)")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Integer userId) {
        try {
            userRepository.deleteById(userId);
            System.out.println("🗑️ Usuario eliminado: " + userId);
            
            return ResponseEntity.ok(
                ApiResponse.success("Usuario eliminado exitosamente", null)
            );
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error("Error al eliminar usuario: " + e.getMessage()));
        }
    }

    @PostMapping("/reset-failed-attempts/{email}")
    @Operation(summary = "Resetear intentos fallidos de un usuario")
    public ResponseEntity<ApiResponse<String>> resetFailedAttempts(@PathVariable String email) {
        try {
            UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            user.setFailedLoginAttempts(0);
            user.setAccountStatus(UserEntity.AccountStatus.ACTIVE);
            user.setAccountLockedUntil(null);
            userRepository.save(user);
            
            System.out.println("🔓 Intentos reseteados para: " + email);
            
            return ResponseEntity.ok(
                ApiResponse.success("Intentos fallidos reseteados", null)
            );
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error("Error: " + e.getMessage()));
        }
    }
}
