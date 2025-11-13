package com.proyecto.synapsevr.Service;

import com.proyecto.synapsevr.dto.Request.SessionRequest;
import com.proyecto.synapsevr.dto.Request.UpdateSessionRequest;
import com.proyecto.synapsevr.dto.Response.SessionResponse;
import com.proyecto.synapsevr.dto.Response.CalendarSessionResponse;
import com.proyecto.synapsevr.dto.Response.FilteredSessionsResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SessionService {
    
    // 📋 Obtener todas las sesiones de un paciente
    List<SessionResponse> getSessionsByPatientId(int patientId);
    
    // 📅 Obtener sesión de HOY de un paciente
    Optional<SessionResponse> getTodaySessionByPatientId(int patientId);
    
    // 🕐 Obtener ÚLTIMA sesión registrada de un paciente
    Optional<SessionResponse> getLatestSessionByPatientId(int patientId);
    
    // ➕ Crear nueva sesión
    SessionResponse createSession(SessionRequest createSession, String userEmail);
    
    // 🗑️ Eliminar sesión
    boolean deleteSession(UUID sessionId, String userEmail);
    
    // 👁️ Obtener sesión por ID (para el botón "Ver")
    Optional<SessionResponse> getSessionById(UUID sessionId);
    
    // 📆 Obtener sesiones para el calendario
    List<CalendarSessionResponse> getSessionsForCalendar(LocalDate startDate, LocalDate endDate);
    
    // 🔍 Obtener sesiones con filtros (HU-006-006)
    FilteredSessionsResponse getSessionsWithFilters(Integer patientId, List<String> exposureLevels, 
                                                   LocalDate dateFrom, LocalDate dateTo, 
                                                   int page, int limit, String sortBy, String sortOrder);
    
    // ✏️ Actualizar sesión existente
    SessionResponse updateSession(UUID sessionId, UpdateSessionRequest updateRequest, String userEmail);
    
    // 📊 Contar sesiones de un paciente
    //long countSessionsByPatientId(Long patientId);
}