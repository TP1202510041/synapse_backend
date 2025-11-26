package com.proyecto.synapsevr.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class VrSessionRequest {

    @NotNull(message = "Patient ID es requerido")
    private Integer patientId;

    @NotNull(message = "La fecha de sesión es requerida")
    private LocalDate sessionDate;

    private String description;

    @NotBlank(message = "El escenario VR es requerido")
    private String vrScenario;

    private String vrDevice;

    private Integer immersionDuration;

    private String movementTrackingData;

    private String environmentSettings;

    private String exposureLevel = "MEDIO";

    private Integer duration;

    private String status;
}