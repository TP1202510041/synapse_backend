package com.proyecto.synapsevr.Repository;

import com.proyecto.synapsevr.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findByEmail(String email);
    
    Optional<UserEntity> findByEmailIgnoreCase(String email);
    
    Optional<UserEntity> findByDni(String dni);
    
    List<UserEntity> findByAccountStatusAndAccountLockedUntilBefore(
        UserEntity.AccountStatus accountStatus, 
        LocalDateTime dateTime
    );
}
