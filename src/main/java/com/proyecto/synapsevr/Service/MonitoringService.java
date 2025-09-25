package com.proyecto.synapsevr.Service;

import com.proyecto.synapsevr.dto.Request.MonitoringRecordRequest;
import com.proyecto.synapsevr.dto.Response.MonitoringRecordResponse;

import java.util.List;
import java.util.UUID;

public interface MonitoringService {

    /**
     * Guarda un registro completo de monitoreo
     * (Se ejecuta cuando se presiona "Detener Monitoreo y Guardar Datos")
     */
    MonitoringRecordResponse saveMonitoringRecord(MonitoringRecordRequest request, int userId);

    /**
     * Obtiene todos los registros de monitoreo de un paciente específico
     * (Para analytics y historial)
     */
    List<MonitoringRecordResponse> getMonitoringRecordsByPatient(int patientId, int userId);

    /**
     * Obtiene un registro específico por ID
     * (Para ver detalles de un monitoreo)
     */
    MonitoringRecordResponse getMonitoringRecord(UUID monitoringId, int userId);

    /**
     * Obtiene todos los registros de monitoreo de un usuario
     * (Para dashboard del terapeuta)
     */
    List<MonitoringRecordResponse> getMonitoringRecordsByUser(int userId);

    /**
     * Obtiene registros de monitoreo por sesión
     * (Para ver monitoreos de una sesión específica)
     */
    List<MonitoringRecordResponse> getMonitoringRecordsBySession(UUID sessionId, int userId);

    /**
     * Elimina un registro de monitoreo
     * (Para el botón eliminar en Analytics)
     */
    void deleteMonitoringRecord(UUID monitoringId, int userId);
}