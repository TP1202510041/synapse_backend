package com.proyecto.synapsevr.dto.Request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class RecordedSessionRequest {
    private UUID sessionId;
    private Integer patientId;
    private String title;
    private String filePath;
    private Long fileSize;
    private Integer durationSeconds;
    private LocalDateTime recordingDate;
    private String description;
}