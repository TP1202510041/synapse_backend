package com.proyecto.synapsevr.dto.Request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SessionRequest {

    private LocalDate sessionDate;
    private String description;
    private int patientId;

    // Constructor vacío
    public SessionRequest() {}

    // Constructor con parámetros
    public SessionRequest(LocalDate sessionDate, String description, int patientId) {
        this.sessionDate = sessionDate;
        this.description = description;
        this.patientId = patientId;
    }
}