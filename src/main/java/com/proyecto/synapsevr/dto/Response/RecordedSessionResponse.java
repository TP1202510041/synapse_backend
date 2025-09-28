package com.proyecto.synapsevr.dto.Response;

import com.proyecto.synapsevr.Entity.RecordedSessionEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class RecordedSessionResponse {
    private Integer id;
    private UUID sessionId;
    private Integer patientId;
    private Integer userId;
    private String title;
    private String filePath;
    private Long fileSize;
    private Integer durationSeconds;
    private LocalDateTime recordingDate;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public RecordedSessionResponse(RecordedSessionEntity entity) {
        this.id = entity.getId();
        this.sessionId = entity.getSessionId();
        this.patientId = entity.getPatientId();
        this.userId = entity.getUserId();
        this.title = entity.getTitle();
        this.filePath = entity.getFilePath();
        this.fileSize = entity.getFileSize();
        this.durationSeconds = entity.getDurationSeconds();
        this.recordingDate = entity.getRecordingDate();
        this.description = entity.getDescription();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
    }
}