package com.proyecto.synapsevr.Repository;

import com.proyecto.synapsevr.Entity.ClinicalObservationEntity;
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
public interface ClinicalObservationRepository extends JpaRepository<ClinicalObservationEntity, UUID> {

    @Query("SELECT co FROM ClinicalObservationEntity co WHERE co.sessionId = :sessionId AND co.isDeleted = false")
    List<ClinicalObservationEntity> findBySessionIdAndIsDeletedFalse(@Param("sessionId") UUID sessionId);

    @Query("SELECT co FROM ClinicalObservationEntity co WHERE co.patientId = :patientId AND co.isDeleted = false")
    Page<ClinicalObservationEntity> findByPatientIdAndIsDeletedFalse(@Param("patientId") Integer patientId, Pageable pageable);

    @Query("SELECT co FROM ClinicalObservationEntity co WHERE co.sessionId = :sessionId AND co.therapistId = :therapistId AND co.isDeleted = false")
    Optional<ClinicalObservationEntity> findBySessionIdAndTherapistIdAndIsDeletedFalse(@Param("sessionId") UUID sessionId, @Param("therapistId") Integer therapistId);

    @Query("SELECT co FROM ClinicalObservationEntity co WHERE co.therapistId = :therapistId AND co.isDeleted = false")
    List<ClinicalObservationEntity> findByTherapistIdAndIsDeletedFalse(@Param("therapistId") Integer therapistId);
}