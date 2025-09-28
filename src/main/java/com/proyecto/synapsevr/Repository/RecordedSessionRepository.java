package com.proyecto.synapsevr.Repository;

import com.proyecto.synapsevr.Entity.RecordedSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecordedSessionRepository extends JpaRepository<RecordedSessionEntity, Integer> {
    
    List<RecordedSessionEntity> findByUserId(Integer userId);
    
    List<RecordedSessionEntity> findByPatientIdAndUserId(Integer patientId, Integer userId);
    
    List<RecordedSessionEntity> findBySessionId(UUID sessionId);
    
    Optional<RecordedSessionEntity> findByIdAndUserId(Integer id, Integer userId);
    
    void deleteByIdAndUserId(Integer id, Integer userId);
}