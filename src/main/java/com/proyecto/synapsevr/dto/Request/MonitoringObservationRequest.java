package com.proyecto.synapsevr.dto.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Schema(description = "Request para crear observación de monitoreo")
public class MonitoringObservationRequest {

    @NotNull(message = "El ID de monitoreo es requerido")
    @Schema(description = "ID del registro de monitoreo", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID monitoringId;

    @NotNull(message = "El ID del paciente es requerido")
    @Schema(description = "ID del paciente", example = "1")
    private Integer patientId;

    @NotBlank(message = "El contenido de la observación es requerido")
    @Size(min = 10, max = 2000, message = "El contenido debe tener entre 10 y 2000 caracteres")
    @Schema(description = "Contenido de la observación clínica", 
            example = "Durante el monitoreo se observó una reducción significativa en el BPM después de los primeros 5 minutos de exposición VR")
    private String content;

    @Schema(description = "Fecha de la observación", example = "2025-10-04")
    private LocalDate observationDate;
}