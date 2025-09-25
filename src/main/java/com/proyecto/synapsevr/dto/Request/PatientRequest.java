package com.proyecto.synapsevr.dto.Request;

import lombok.Data;

@Data
public class PatientRequest {

    private String patientName;  // Coincide con tu campo
    private int age;
    private String gender;
    private String imageUrl;
}
