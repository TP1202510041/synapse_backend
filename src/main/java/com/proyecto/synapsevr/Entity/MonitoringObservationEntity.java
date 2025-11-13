package com.proyecto.synapsevr.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "monitoring_observations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonitoringObservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "monitoring_id", nullable = false)
    private UUID monitoringId;

    @Column(name = "patient_id", nullable = false)
    private Integer patientId;

    @Column(name = "therapist_id", nullable = false)
    private Integer therapistId;

    @Column(name = "content", nullable = false, length = 2000)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "version", nullable = false)
    private Integer version = 1;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    // Relaciones JPA (opcional, para navegación)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monitoring_id", insertable = false, updatable = false)
    private MonitoringRecordEntity monitoringRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", insertable = false, updatable = false)
    private PatientEntity patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "therapist_id", insertable = false, updatable = false)
    private UserEntity therapist;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (version == null) {
            version = 1;
        }
        if (isDeleted == null) {
            isDeleted = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}