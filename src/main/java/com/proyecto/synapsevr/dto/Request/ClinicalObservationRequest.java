package com.proyecto.synapsevr.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class ClinicalObservationRequest {

    @NotNull(message = "Session ID es requerido")
    private UUID sessionId;

    @NotNull(message = "Patient ID es requerido")
    private Integer patientId;

    // ← REMOVIDO: therapistId - se obtiene del token JWT automáticamente

    @NotBlank(message = "El contenido es requerido")
    @Size(min = 10, max = 2000, message = "El contenido debe tener entre 10 y 2000 caracteres")
    private String content;

    private LocalDate sessionDate;
}