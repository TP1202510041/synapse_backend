package com.proyecto.synapsevr.Service;

import com.proyecto.synapsevr.Entity.UserEntity;

import java.util.Optional;

public interface SessionService {

    UserEntity registerUser(String email, String userName, String password);
    UserEntity loginUser(String email, String password);
    Optional<UserEntity> findByEmail(String email);
}
