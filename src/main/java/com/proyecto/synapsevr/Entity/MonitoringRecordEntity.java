package com.proyecto.synapsevr.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "monitoringrecords")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonitoringRecordEntity {

    @Id
    @Column(name = "monitoring_id")
    private UUID monitoringId;

    @Column(name = "start_time", nullable = false)
    private Long startTime;

    @Column(name = "end_time", nullable = false)
    private Long endTime;

    @Column(name = "start_time_local", nullable = false)
    private Long startTimeLocal;

    @Column(name = "end_time_local", nullable = false)
    private Long endTimeLocal;

    @Column(name = "duration", nullable = false)
    private Integer duration;

    @Column(name = "total_records", nullable = false)
    private Integer totalRecords;

    @Column(name = "avg_heart_rate", precision = 5, scale = 2)
    private BigDecimal avgHeartRate;

    @Column(name = "min_heart_rate", precision = 5, scale = 2)
    private BigDecimal minHeartRate;

    @Column(name = "max_heart_rate", precision = 5, scale = 2)
    private BigDecimal maxHeartRate;

    @Column(name = "mongo_document_id")
    private String mongoDocumentId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Foreign Keys
    @Column(name = "id_session_fk")
    private UUID sessionId;

    @Column(name = "patient_id_fk", nullable = false)
    private Integer patientId;

    @Column(name = "user_id_fk", nullable = false)
    private Integer userId;

    // Relaciones JPA (opcional, para navegación)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_session_fk", insertable = false, updatable = false)
    private SessionEntity session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id_fk", insertable = false, updatable = false)
    private PatientEntity patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id_fk", insertable = false, updatable = false)
    private UserEntity user;

    @PrePersist
    protected void onCreate() {
        if (monitoringId == null) {
            monitoringId = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}