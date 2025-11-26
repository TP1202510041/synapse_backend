package com.proyecto.synapsevr.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateObservationRequest {

    @NotBlank(message = "El contenido es requerido")
    @Size(min = 10, max = 2000, message = "El contenido debe tener entre 10 y 2000 caracteres")
    private String content;

    @NotNull(message = "La versión es requerida para control de concurrencia")
    private Integer version;
}