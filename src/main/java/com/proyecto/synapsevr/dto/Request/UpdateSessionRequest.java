package com.proyecto.synapsevr.dto.Request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateSessionRequest {

    private LocalDate sessionDate;
    private String description;

    // Constructor vacío
    public UpdateSessionRequest() {}

    // Constructor con parámetros
    public UpdateSessionRequest(LocalDate sessionDate, String description) {
        this.sessionDate = sessionDate;
        this.description = description;
    }
}