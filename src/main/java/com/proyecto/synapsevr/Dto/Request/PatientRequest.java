package com.proyecto.synapsevr.Dto.Request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class PatientRequest {

    private String patientName;  // Coincide con tu campo
    private int age;
    private String gender;
    private String imageUrl;
}
