package com.proyecto.synapsevr.Repository;

import com.proyecto.synapsevr.Entity.LoginAttemptEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttemptEntity, Long> {
    
    List<LoginAttemptEntity> findByEmailAndAttemptTimeAfter(String email, LocalDateTime time);
    
    List<LoginAttemptEntity> findByEmailOrderByAttemptTimeDesc(String email);
    
    void deleteByAttemptTimeBefore(LocalDateTime time);
    
    long countByEmailAndSuccessfulAndAttemptTimeAfter(String email, Boolean successful, LocalDateTime time);
}
