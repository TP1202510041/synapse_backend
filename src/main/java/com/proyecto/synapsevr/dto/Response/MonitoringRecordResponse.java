package com.proyecto.synapsevr.dto.Response;

import com.proyecto.synapsevr.Entity.MonitoringRecordEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class MonitoringRecordResponse {

    private UUID monitoringId;
    private UUID sessionId;
    private Integer patientId;
    private String patientName;
    private Integer userId;
    private Long startTime;
    private Long endTime;
    private Long startTimeLocal;
    private Long endTimeLocal;
    private Integer duration;
    private Integer totalRecords;
    private BigDecimal avgHeartRate;
    private BigDecimal minHeartRate;
    private BigDecimal maxHeartRate;
    private String mongoDocumentId;
    private LocalDateTime createdAt;

    // Constructor desde Entity
    public MonitoringRecordResponse(MonitoringRecordEntity entity) {
        this.monitoringId = entity.getMonitoringId();
        this.sessionId = entity.getSessionId();
        this.patientId = entity.getPatientId();
        this.userId = entity.getUserId();
        this.startTime = entity.getStartTime();
        this.endTime = entity.getEndTime();
        this.startTimeLocal = entity.getStartTimeLocal();
        this.endTimeLocal = entity.getEndTimeLocal();
        this.duration = entity.getDuration();
        this.totalRecords = entity.getTotalRecords();
        this.avgHeartRate = entity.getAvgHeartRate();
        this.minHeartRate = entity.getMinHeartRate();
        this.maxHeartRate = entity.getMaxHeartRate();
        this.mongoDocumentId = entity.getMongoDocumentId();
        this.createdAt = entity.getCreatedAt();
        
        // Si la entidad tiene la relación cargada, obtener el nombre del paciente
        if (entity.getPatient() != null) {
            this.patientName = entity.getPatient().getPatientName();
        }
    }
}