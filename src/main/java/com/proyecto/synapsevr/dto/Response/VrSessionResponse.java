package com.proyecto.synapsevr.dto.Response;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class VrSessionResponse {

    private UUID sessionId;
    private LocalDate sessionDate;
    private String description;
    private String exposureLevel;
    private Integer duration;
    private String status;
    private Integer patientId;
    private String patientName;
    
    // VR specific fields
    private UUID vrSessionId;
    private String vrScenario;
    private String vrDevice;
    private Integer immersionDuration;
    private String movementTrackingData;
    private String environmentSettings;
}