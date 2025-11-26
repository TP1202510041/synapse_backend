package com.proyecto.synapsevr.Repository;

import com.proyecto.synapsevr.Entity.ExportLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ExportLogRepository extends JpaRepository<ExportLogEntity, UUID> {

    @Query("SELECT el FROM ExportLogEntity el WHERE el.patientId = :patientId ORDER BY el.createdAt DESC")
    List<ExportLogEntity> findByPatientIdOrderByCreatedAtDesc(@Param("patientId") Integer patientId);

    @Query("SELECT el FROM ExportLogEntity el WHERE el.therapistId = :therapistId ORDER BY el.createdAt DESC")
    List<ExportLogEntity> findByTherapistIdOrderByCreatedAtDesc(@Param("therapistId") Integer therapistId);

    @Query("SELECT el FROM ExportLogEntity el WHERE el.expiresAt <= :now")
    List<ExportLogEntity> findExpiredExports(@Param("now") LocalDateTime now);

    @Query("SELECT el FROM ExportLogEntity el WHERE el.status = :status")
    List<ExportLogEntity> findByStatus(@Param("status") ExportLogEntity.ExportStatus status);
}