package com.proyecto.synapsevr.Service.ServiceImpl;

import com.proyecto.synapsevr.Entity.UserEntity;
import com.proyecto.synapsevr.Service.SessionService;

import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class SessionServiceImpl implements SessionService {
    @Override
    public UserEntity registerUser(String email, String userName, String password) {
        return null;
    }

    @Override
    public UserEntity loginUser(String email, String password) {
        return null;
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return Optional.empty();
    }
}
