package com.proyecto.synapsevr.Service;

import com.proyecto.synapsevr.dto.Request.ClinicalObservationRequest;
import com.proyecto.synapsevr.dto.Request.UpdateObservationRequest;
import com.proyecto.synapsevr.dto.Response.ClinicalObservationResponse;
import com.proyecto.synapsevr.dto.Response.PaginatedObservationsResponse;

import java.util.List;
import java.util.UUID;

public interface ClinicalObservationService {

    ClinicalObservationResponse createObservation(ClinicalObservationRequest request, String userEmail);

    ClinicalObservationResponse updateObservation(UUID observationId, UpdateObservationRequest request, String userEmail);

    List<ClinicalObservationResponse> getObservationsBySession(UUID sessionId);

    PaginatedObservationsResponse getObservationsByPatient(Integer patientId, int page, int limit, String sortBy, String sortOrder);

    void deleteObservation(UUID observationId, String userEmail);
}