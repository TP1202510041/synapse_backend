package com.proyecto.synapsevr.Service.ServiceImpl;

import com.proyecto.synapsevr.dto.Request.LoginRequest;
import com.proyecto.synapsevr.dto.Request.RegisterRequest;
import com.proyecto.synapsevr.dto.Response.AuthResponse;
import com.proyecto.synapsevr.Entity.UserEntity;
import com.proyecto.synapsevr.Repository.UserRepository;
import com.proyecto.synapsevr.Repository.LoginAttemptRepository;
import com.proyecto.synapsevr.Security.JwtConfig;
import com.proyecto.synapsevr.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtConfig jwtConfig;
    private final AuthenticationManager authenticationManager;
    private final LoginAttemptRepository loginAttemptRepository;

    @Override
    public AuthResponse registerUser(RegisterRequest request) {
        // Verificar si el email ya existe
        if (emailExists(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Verificar si el DNI ya existe
        if (request.getDni() != null && userRepository.findByDni(request.getDni()).isPresent()) {
            throw new RuntimeException("El DNI ya está registrado");
        }

        // Crear nuevo usuario con todos los campos
        UserEntity newUser = new UserEntity();
        newUser.setEmail(request.getEmail());
        newUser.setUserName(request.getUserName());
        newUser.setUserPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setPhoneNumber(request.getPhoneNumber());
        newUser.setDni(request.getDni());
        newUser.setAddress(request.getAddress());
        newUser.setBirthDate(request.getBirthDate());
        newUser.setGender(request.getGender());
        newUser.setProfessionalLicense(request.getProfessionalLicense());
        newUser.setSpecialization(request.getSpecialization());
        newUser.setRole(request.getRole() != null ? request.getRole() : UserEntity.Role.USER);
        newUser.setAccountStatus(UserEntity.AccountStatus.ACTIVE);
        newUser.setEmailVerified(false);
        newUser.setFailedLoginAttempts(0);
        newUser.setPasswordChangedAt(LocalDateTime.now());

        // Guardar usuario
        UserEntity savedUser = userRepository.save(newUser);

        // Generar token JWT
        String jwt = jwtConfig.generateToken(savedUser);

        return AuthResponse.builder()
                .token(jwt)
                .email(savedUser.getEmail())
                .userName(savedUser.getUsername())
                .role(savedUser.getRole().name())
                .userId(savedUser.getUserId())
                .message("Usuario registrado exitosamente")
                .build();
    }

    @Override
    public AuthResponse loginUser(LoginRequest request) {
        // Autenticar usuario
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Buscar usuario en BD
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Generar token JWT
        String jwt = jwtConfig.generateToken(user);

        return AuthResponse.builder()
                .token(jwt)
                .email(user.getEmail())
                .userName(user.getUsername())
                .role(user.getRole().name())
                .userId(user.getUserId())  // ← AGREGADO: userId crítico en login
                .message("Login exitoso")
                .build();
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    // Métodos de reset password movidos a PasswordResetService
    // para usar sistema de códigos de 6 dígitos en lugar de UUID

    @Override
    public List<Object> getLoginAttempts(Integer userId) {
        // Buscar usuario por ID para obtener su email
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Buscar intentos por email
        return loginAttemptRepository.findByEmailOrderByAttemptTimeDesc(user.getEmail())
                .stream()
                .map(attempt -> (Object) attempt)
                .toList();
    }

    // Método adicional para validar login simple (sin JWT)
    public boolean validateLogin(String email, String password) {
        Optional<UserEntity> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return passwordEncoder.matches(password, user.get().getUserPassword());
        }
        return false;
    }
}