package com.proyecto.synapsevr.Repository;

import com.proyecto.synapsevr.Entity.PasswordResetTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, Long> {

    Optional<PasswordResetTokenEntity> findByTokenAndUsedFalse(String token);

    Optional<PasswordResetTokenEntity> findByEmailAndUsedFalseOrderByCreatedAtDesc(String email);

    List<PasswordResetTokenEntity> findByEmailAndCreatedAtAfter(String email, LocalDateTime after);

    void deleteByExpiresAtBefore(LocalDateTime dateTime);

    long countByEmailAndCreatedAtAfter(String email, LocalDateTime after);
}
