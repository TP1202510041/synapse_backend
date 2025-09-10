package com.proyecto.synapsevr.Service.ServiceImpl;

import com.proyecto.synapsevr.Entity.UserEntity;
import com.proyecto.synapsevr.Repository.UserRepository;
import com.proyecto.synapsevr.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean registerUser(String email, String userName, String password) {

        if(emailExists(email)){
          return false;
        }

        UserEntity newUser = new UserEntity();
        newUser.setUserName(userName);
        newUser.setUserPassword(passwordEncoder.encode(password));
        newUser.setEmail(email);

        userRepository.save(newUser);
        return true;
    }

    @Override
    public boolean loginUser(String email, String password) {
        Optional<UserEntity> user = userRepository.findByEmail(email);
        if(user.isPresent()){
            return passwordEncoder.matches(password, user.get().getUserPassword());
        }
        return false;
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

}
