package com.proyecto.synapsevr.Repository;

import com.proyecto.synapsevr.Entity.VrSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VrSessionRepository extends JpaRepository<VrSessionEntity, UUID> {

    Optional<VrSessionEntity> findBySessionId(UUID sessionId);

    @Query("SELECT vs FROM VrSessionEntity vs JOIN vs.session s WHERE s.patient.patientId = :patientId ORDER BY s.sessionDate DESC")
    List<VrSessionEntity> findByPatientIdOrderBySessionDateDesc(@Param("patientId") Integer patientId);

    @Query("SELECT vs FROM VrSessionEntity vs WHERE vs.vrScenario = :scenario")
    List<VrSessionEntity> findByVrScenario(@Param("scenario") String scenario);

    @Query("SELECT vs FROM VrSessionEntity vs WHERE vs.vrDevice = :device")
    List<VrSessionEntity> findByVrDevice(@Param("device") String device);
}