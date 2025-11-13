package com.proyecto.synapsevr.Repository;

import com.proyecto.synapsevr.Entity.SessionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<SessionEntity, UUID> {
    
    // 📋 Listar todas las sesiones de un paciente (ordenadas por fecha desc)
    List<SessionEntity> findByPatientPatientIdOrderBySessionDateDesc(int patientId);
    
    // 📅 Buscar sesión de HOY de un paciente (usando CAST)
    @Query("SELECT s FROM SessionEntity s WHERE s.patient.patientId = :patientId AND CAST(s.sessionDate AS DATE) = CURRENT_DATE ORDER BY s.sessionDate ASC")
    Optional<SessionEntity> findTodaySessionByPatientId(@Param("patientId") int patientId);

    @Query("SELECT s FROM SessionEntity s WHERE s.patient.patientId = :patientId ORDER BY s.idSession DESC LIMIT 1")
    Optional<SessionEntity> findLatestSessionByPatientId(@Param("patientId") int patientId);

    // 📆 Para el calendario: sesiones en rango de fechas (usando CAST)
    @Query("SELECT s FROM SessionEntity s WHERE CAST(s.sessionDate AS DATE) BETWEEN :startDate AND :endDate ORDER BY s.sessionDate ASC")
    List<SessionEntity> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // 🔍 Filtrado por nivel de exposición y fechas con paginación
    @Query("SELECT s FROM SessionEntity s WHERE s.patient.patientId = :patientId " +
           "AND (:exposureLevels IS NULL OR s.exposureLevel IN :exposureLevels) " +
           "AND (:dateFrom IS NULL OR s.sessionDate >= :dateFrom) " +
           "AND (:dateTo IS NULL OR s.sessionDate <= :dateTo)")
    Page<SessionEntity> findByPatientIdWithFilters(
        @Param("patientId") Integer patientId,
        @Param("exposureLevels") List<SessionEntity.ExposureLevel> exposureLevels,
        @Param("dateFrom") LocalDate dateFrom,
        @Param("dateTo") LocalDate dateTo,
        Pageable pageable
    );

    // 📊 Para análisis y métricas
    @Query("SELECT s FROM SessionEntity s WHERE s.patient.patientId = :patientId ORDER BY s.sessionDate ASC")
    List<SessionEntity> findByPatientIdOrderBySessionDateAsc(@Param("patientId") Integer patientId);

    // 🎮 Sesiones VR específicas
    @Query("SELECT s FROM SessionEntity s JOIN s.vrSession vs WHERE s.patient.patientId = :patientId " +
           "AND (:vrScenario IS NULL OR vs.vrScenario = :vrScenario) " +
           "AND (:dateFrom IS NULL OR s.sessionDate >= :dateFrom) " +
           "AND (:dateTo IS NULL OR s.sessionDate <= :dateTo)")
    Page<SessionEntity> findVrSessionsByPatientIdWithFilters(
        @Param("patientId") Integer patientId,
        @Param("vrScenario") String vrScenario,
        @Param("dateFrom") LocalDate dateFrom,
        @Param("dateTo") LocalDate dateTo,
        Pageable pageable
    );
}