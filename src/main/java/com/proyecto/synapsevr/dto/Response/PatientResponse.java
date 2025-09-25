package com.proyecto.synapsevr.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponse {
    private int patientId;
    private String patientName;
    private int age;
    private String gender;
    private String imageUrl;
    private String userName; // user.getUserName()
    private int totalSessions;    // sessions.size() - opcional
}
