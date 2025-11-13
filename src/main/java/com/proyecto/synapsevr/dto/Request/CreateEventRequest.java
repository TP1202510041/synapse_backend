package com.proyecto.synapsevr.dto.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Request mínimo para crear evento del calendario")
public class CreateEventRequest {
    
    @NotBlank(message = "El título es requerido")
    @Schema(description = "Título del evento", example = "Cita con paciente", required = true)
    private String title;
    
    @NotNull(message = "La fecha de inicio es requerida")
    @Schema(description = "Fecha y hora de inicio del evento", example = "2025-10-05T10:00:00", required = true)
    private LocalDateTime start;
    
    @NotNull(message = "La fecha de fin es requerida")
    @Schema(description = "Fecha y hora de fin del evento", example = "2025-10-05T11:00:00", required = true)
    private LocalDateTime end;
}