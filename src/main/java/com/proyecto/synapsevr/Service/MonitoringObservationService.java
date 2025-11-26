package com.proyecto.synapsevr.Service;

import com.proyecto.synapsevr.dto.Request.MonitoringObservationRequest;
import com.proyecto.synapsevr.dto.Response.MonitoringObservationResponse;
import com.proyecto.synapsevr.dto.Response.PaginatedMonitoringObservationsResponse;

import java.util.List;
import java.util.UUID;

public interface MonitoringObservationService {

    /**
     * Crear nueva observación de monitoreo
     */
    MonitoringObservationResponse createMonitoringObservation(MonitoringObservationRequest request, String userEmail);

    /**
     * Obtener observaciones por monitoring ID
     */
    List<MonitoringObservationResponse> getObservationsByMonitoringId(UUID monitoringId);

    /**
     * Obtener observaciones por paciente (paginado)
     */
    PaginatedMonitoringObservationsResponse getObservationsByPatient(Integer patientId, int page, int limit, String sortBy, String sortOrder);

    /**
     * Actualizar observación de monitoreo
     */
    MonitoringObservationResponse updateMonitoringObservation(UUID observationId, MonitoringObservationRequest request, String userEmail);

    /**
     * Eliminar observación de monitoreo (soft delete)
     */
    void deleteMonitoringObservation(UUID observationId, String userEmail);

    /**
     * Obtener observaciones por terapeuta
     */
    List<MonitoringObservationResponse> getObservationsByTherapist(String userEmail);
}