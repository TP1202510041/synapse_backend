package com.proyecto.synapsevr.Service;

import com.proyecto.synapsevr.dto.Request.LoginRequest;
import com.proyecto.synapsevr.dto.Request.RegisterRequest;
import com.proyecto.synapsevr.dto.Response.AuthResponse;

public interface AuthService {
    
    AuthResponse registerUser(RegisterRequest request);
    
    AuthResponse loginUser(LoginRequest request);
    
    boolean emailExists(String email);
}