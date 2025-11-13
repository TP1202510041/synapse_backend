package com.proyecto.synapsevr.Service;

import com.proyecto.synapsevr.dto.Request.LoginRequest;
import com.proyecto.synapsevr.dto.Request.RegisterRequest;
import com.proyecto.synapsevr.dto.Response.AuthResponse;

import java.util.List;

public interface AuthService {
    
    AuthResponse registerUser(RegisterRequest request);
    
    AuthResponse loginUser(LoginRequest request);
    
    boolean emailExists(String email);

    List<Object> getLoginAttempts(Integer userId);
}