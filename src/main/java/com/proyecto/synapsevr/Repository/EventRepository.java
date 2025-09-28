package com.proyecto.synapsevr.Repository;

import com.proyecto.synapsevr.Entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Integer> {
    
    List<EventEntity> findByUserId(Integer userId);
    
    Optional<EventEntity> findByIdAndUserId(Integer id, Integer userId);
    
    @Query("SELECT e FROM EventEntity e WHERE e.userId = :userId AND " +
           "((e.start BETWEEN :startDate AND :endDate) OR " +
           "(e.end BETWEEN :startDate AND :endDate) OR " +
           "(e.start <= :startDate AND e.end >= :endDate))")
    List<EventEntity> findByUserIdAndDateRange(
        @Param("userId") Integer userId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    void deleteByIdAndUserId(Integer id, Integer userId);
}