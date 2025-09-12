package com.proyecto.synapsevr.Service;

import com.proyecto.synapsevr.Dto.Request.LoginRequest;
import com.proyecto.synapsevr.Dto.Request.RegisterRequest;
import com.proyecto.synapsevr.Dto.Response.AuthResponse;

public interface AuthService {
    
    AuthResponse registerUser(RegisterRequest request);
    
    AuthResponse loginUser(LoginRequest request);
    
    boolean emailExists(String email);
}