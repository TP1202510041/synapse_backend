package com.proyecto.synapsevr.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "export_logs")
@NoArgsConstructor
@AllArgsConstructor
public class ExportLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "patient_id", nullable = false)
    private Integer patientId;

    @Column(name = "therapist_id", nullable = false)
    private Integer therapistId;

    @Column(name = "export_type", nullable = false, length = 50)
    private String exportType;

    @Column(name = "file_path", length = 500)
    private String filePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ExportStatus status = ExportStatus.PROCESSING;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", insertable = false, updatable = false)
    private PatientEntity patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "therapist_id", insertable = false, updatable = false)
    private UserEntity therapist;

    public enum ExportStatus {
        PROCESSING, COMPLETED, FAILED
    }
}