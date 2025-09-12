package com.proyecto.synapsevr.Service;

import com.proyecto.synapsevr.Dto.Request.PatientRequest;
import com.proyecto.synapsevr.Dto.Response.PatientResponse;

import java.util.List;

public interface PatientService {

    List<PatientResponse> getAllPatients();
    PatientResponse getPatientById(int patientId);
    PatientResponse createPatient(PatientRequest request);
    PatientResponse updatePatient(int patientId, PatientRequest request);
    void deletePatient(int patientId);
}