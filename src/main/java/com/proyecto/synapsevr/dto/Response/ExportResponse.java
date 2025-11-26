package com.proyecto.synapsevr.dto.Response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ExportResponse {

    private UUID exportId;
    private String status; // "processing", "completed", "failed"
    private String downloadUrl;
    private LocalDateTime expiresAt;
}