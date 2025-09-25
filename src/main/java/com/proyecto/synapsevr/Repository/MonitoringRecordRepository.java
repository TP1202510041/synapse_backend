package com.proyecto.synapsevr.Repository;

import com.proyecto.synapsevr.Entity.MonitoringRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MonitoringRecordRepository extends JpaRepository<MonitoringRecordEntity, UUID> {

    // Buscar por sesión específica
    List<MonitoringRecordEntity> findBySessionId(UUID sessionId);

    // Buscar por paciente y usuario (para seguridad)
    List<MonitoringRecordEntity> findByPatientIdAndUserId(Integer patientId, Integer userId);

    // Buscar por ID con validación de usuario
    Optional<MonitoringRecordEntity> findByMonitoringIdAndUserId(UUID monitoringId, Integer userId);

    // Buscar todos los registros de un usuario
    List<MonitoringRecordEntity> findByUserId(Integer userId);

    // Para analytics - último registro de un paciente
    @Query("SELECT m FROM MonitoringRecordEntity m WHERE m.patientId = :patientId AND m.userId = :userId ORDER BY m.createdAt DESC")
    Optional<MonitoringRecordEntity> findLatestByPatientIdAndUserId(@Param("patientId") Integer patientId, @Param("userId") Integer userId);

    // Contar registros por usuario
    long countByUserId(Integer userId);
}