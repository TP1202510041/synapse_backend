package com.proyecto.synapsevr.Repository;

import com.proyecto.synapsevr.Entity.AuditLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {

    @Query("SELECT al FROM AuditLogEntity al WHERE al.entityType = :entityType AND al.entityId = :entityId ORDER BY al.timestamp DESC")
    List<AuditLogEntity> findByEntityTypeAndEntityIdOrderByTimestampDesc(@Param("entityType") String entityType, @Param("entityId") String entityId);

    @Query("SELECT al FROM AuditLogEntity al WHERE al.userId = :userId ORDER BY al.timestamp DESC")
    Page<AuditLogEntity> findByUserIdOrderByTimestampDesc(@Param("userId") Integer userId, Pageable pageable);

    @Query("SELECT al FROM AuditLogEntity al WHERE al.timestamp BETWEEN :startDate AND :endDate ORDER BY al.timestamp DESC")
    List<AuditLogEntity> findByTimestampBetweenOrderByTimestampDesc(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}