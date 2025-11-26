package com.proyecto.synapsevr.Service;

import com.proyecto.synapsevr.dto.Request.VrSessionRequest;
import com.proyecto.synapsevr.dto.Response.FilteredSessionsResponse;
import com.proyecto.synapsevr.dto.Response.VrSessionResponse;

import java.time.LocalDate;
import java.util.List;

public interface VrSessionService {

    VrSessionResponse createVrSession(VrSessionRequest request, String userEmail);

    FilteredSessionsResponse getVrSessionsByPatient(Integer patientId, String vrScenario, 
                                                   LocalDate dateFrom, LocalDate dateTo, 
                                                   int page, int limit);

    List<VrSessionResponse> getVrSessionsByScenario(String scenario);

    List<VrSessionResponse> getVrSessionsByDevice(String device);
}