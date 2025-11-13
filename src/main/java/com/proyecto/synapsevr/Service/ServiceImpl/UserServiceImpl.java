package com.proyecto.synapsevr.Service.ServiceImpl;

import com.proyecto.synapsevr.Entity.UserEntity;
import com.proyecto.synapsevr.Repository.UserRepository;
import com.proyecto.synapsevr.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    
    @Override
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
    }
}
