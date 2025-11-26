package com.proyecto.synapsevr.dto.Response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ClinicalObservationResponse {

    private UUID id;
    private UUID sessionId;
    private Integer patientId;
    private Integer therapistId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer version;
    private String therapistName;
    private String patientName;
}