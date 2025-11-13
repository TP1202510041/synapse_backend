package com.proyecto.synapsevr.Repository;

import com.proyecto.synapsevr.Entity.MonitoringObservationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MonitoringObservationRepository extends JpaRepository<MonitoringObservationEntity, UUID> {

    /**
     * Buscar observaciones por monitoring ID (no eliminadas)
     */
    List<MonitoringObservationEntity> findByMonitoringIdAndIsDeletedFalse(UUID monitoringId);

    /**
     * Buscar observaciones por paciente (no eliminadas)
     */
    Page<MonitoringObservationEntity> findByPatientIdAndIsDeletedFalse(Integer patientId, Pageable pageable);

    /**
     * Buscar observaciones por terapeuta (no eliminadas)
     */
    List<MonitoringObservationEntity> findByTherapistIdAndIsDeletedFalse(Integer therapistId);

    /**
     * Buscar observaciones por monitoring ID y terapeuta (para validaciones)
     */
    Optional<MonitoringObservationEntity> findByMonitoringIdAndTherapistIdAndIsDeletedFalse(UUID monitoringId, Integer therapistId);

    /**
     * Contar observaciones por monitoring ID
     */
    @Query("SELECT COUNT(mo) FROM MonitoringObservationEntity mo WHERE mo.monitoringId = :monitoringId AND mo.isDeleted = false")
    Long countByMonitoringIdAndIsDeletedFalse(@Param("monitoringId") UUID monitoringId);

    /**
     * Buscar observaciones por paciente en un rango de fechas
     */
    @Query("SELECT mo FROM MonitoringObservationEntity mo WHERE mo.patientId = :patientId AND mo.isDeleted = false AND mo.createdAt BETWEEN :startDate AND :endDate ORDER BY mo.createdAt DESC")
    List<MonitoringObservationEntity> findByPatientIdAndDateRange(@Param("patientId") Integer patientId, 
                                                                  @Param("startDate") java.time.LocalDateTime startDate, 
                                                                  @Param("endDate") java.time.LocalDateTime endDate);

    /**
     * Buscar todas las observaciones de monitoreo de un paciente (ordenadas por fecha)
     */
    List<MonitoringObservationEntity> findByPatientIdAndIsDeletedFalseOrderByCreatedAtDesc(Integer patientId);
}