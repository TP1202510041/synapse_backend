package com.proyecto.synapsevr.Service;

public interface AuthService {

    boolean registerUser(String email, String userName, String password);
    boolean loginUser(String email, String password);
    boolean emailExists(String email);
}
