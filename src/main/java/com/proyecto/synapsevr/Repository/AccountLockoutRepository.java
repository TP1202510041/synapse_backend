package com.proyecto.synapsevr.Repository;

import com.proyecto.synapsevr.Entity.AccountLockoutEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountLockoutRepository extends JpaRepository<AccountLockoutEntity, Long> {

    @Query("SELECT al FROM AccountLockoutEntity al WHERE al.userId = :userId AND al.unlockAt > :now ORDER BY al.lockedAt DESC")
    Optional<AccountLockoutEntity> findActiveByUserId(@Param("userId") Integer userId, @Param("now") LocalDateTime now);

    @Query("SELECT al FROM AccountLockoutEntity al WHERE al.ipAddress = :ipAddress AND al.unlockAt > :now ORDER BY al.lockedAt DESC")
    Optional<AccountLockoutEntity> findActiveByIpAddress(@Param("ipAddress") String ipAddress, @Param("now") LocalDateTime now);

    @Query("SELECT al FROM AccountLockoutEntity al WHERE al.userId = :userId ORDER BY al.lockedAt DESC")
    List<AccountLockoutEntity> findByUserIdOrderByLockedAtDesc(@Param("userId") Integer userId);

    @Query("SELECT al FROM AccountLockoutEntity al WHERE al.unlockAt <= :now AND al.unlockedBy IS NULL")
    List<AccountLockoutEntity> findExpiredLockouts(@Param("now") LocalDateTime now);
}