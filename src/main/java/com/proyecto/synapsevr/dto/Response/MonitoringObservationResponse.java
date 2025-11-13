package com.proyecto.synapsevr.dto.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Schema(description = "Response de observación de monitoreo")
public class MonitoringObservationResponse {

    @Schema(description = "ID único de la observación", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "ID del registro de monitoreo", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID monitoringId;

    @Schema(description = "ID del paciente", example = "1")
    private Integer patientId;

    @Schema(description = "ID del terapeuta", example = "1")
    private Integer therapistId;

    @Schema(description = "Contenido de la observación", 
            example = "Durante el monitoreo se observó una reducción significativa en el BPM después de los primeros 5 minutos")
    private String content;

    @Schema(description = "Fecha y hora de creación", example = "2025-10-04T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha y hora de última actualización", example = "2025-10-04T10:30:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Versión de la observación", example = "1")
    private Integer version;

    @Schema(description = "Nombre del terapeuta", example = "Dr. Juan Pérez")
    private String therapistName;

    @Schema(description = "Nombre del paciente", example = "María García")
    private String patientName;

    @Schema(description = "Información del monitoreo asociado")
    private MonitoringInfo monitoringInfo;

    @Data
    @Schema(description = "Información básica del monitoreo")
    public static class MonitoringInfo {
        @Schema(description = "Duración del monitoreo en minutos", example = "45")
        private Integer duration;

        @Schema(description = "BPM promedio durante el monitoreo", example = "82.5")
        private Double avgHeartRate;

        @Schema(description = "BPM máximo durante el monitoreo", example = "120.0")
        private Double maxHeartRate;

        @Schema(description = "Fecha de creación del monitoreo", example = "2025-10-04T10:00:00")
        private LocalDateTime monitoringCreatedAt;
    }
}