package com.proyecto.synapsevr.dto.Response;

import com.proyecto.synapsevr.Entity.SessionEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class SessionResponse {

    private UUID idSession;
    private LocalDate sessionDate;
    private String description;
    private int patientId;
    private String patientName;
    private String estado; // "PROGRAMADA", "EXPIRADA", "ACTIVA"

    // Constructor vacío
    public SessionResponse() {}

    // Constructor desde Entity CON LÓGICA DE ESTADO AUTOMÁTICO
    public SessionResponse(SessionEntity session) {
        this.idSession = session.getIdSession();
        this.sessionDate = session.getSessionDate();
        this.description = session.getDescription();
        this.patientId = session.getPatient().getPatientId();
        this.patientName = session.getPatient().getPatientName();
    }
}
