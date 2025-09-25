package com.proyecto.synapsevr.dto.Response;

import com.proyecto.synapsevr.Entity.SessionEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class CalendarSessionResponse {

    private UUID idSession;
    private LocalDate sessionDate;
    private String description;
    private String patientName;

    // Constructor vacío
    public CalendarSessionResponse() {}

    // Constructor desde Entity
    public CalendarSessionResponse(SessionEntity session) {
        this.idSession = session.getIdSession();
        this.sessionDate = session.getSessionDate();
        this.description = session.getDescription();
        this.patientName = session.getPatient().getPatientName();
    }
}