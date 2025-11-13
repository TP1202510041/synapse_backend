package com.proyecto.synapsevr.Service;

import com.proyecto.synapsevr.Entity.UserEntity;

public interface UserService {
    UserEntity findByEmail(String email);
}
