package com.proyecto.synapsevr.Service.ServiceImpl;

import com.proyecto.synapsevr.dto.Request.LoginRequest;
import com.proyecto.synapsevr.dto.Request.RegisterRequest;
import com.proyecto.synapsevr.dto.Response.AuthResponse;
import com.proyecto.synapsevr.Entity.UserEntity;
import com.proyecto.synapsevr.Repository.UserRepository;
import com.proyecto.synapsevr.Security.JwtConfig;
import com.proyecto.synapsevr.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtConfig jwtConfig;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse registerUser(RegisterRequest request) {
        // Verificar si el email ya existe
        if (emailExists(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Crear nuevo usuario
        UserEntity newUser = new UserEntity();
        newUser.setEmail(request.getEmail());
        newUser.setUserName(request.getUserName());
        newUser.setUserPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole(UserEntity.Role.USER); // Por defecto USER

        // Guardar usuario
        UserEntity savedUser = userRepository.save(newUser);

        // Generar token JWT
        String jwt = jwtConfig.generateToken(savedUser);

        return AuthResponse.builder()
                .token(jwt)
                .email(savedUser.getEmail())
                .userName(savedUser.getUsername())
                .role(savedUser.getRole().name())
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
                .message("Login exitoso")
                .build();
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
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