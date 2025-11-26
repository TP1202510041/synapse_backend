package com.proyecto.synapsevr.Service.ServiceImpl;

import com.proyecto.synapsevr.dto.Request.SessionRequest;
import com.proyecto.synapsevr.dto.Request.UpdateSessionRequest;
import com.proyecto.synapsevr.dto.Response.SessionResponse;
import com.proyecto.synapsevr.dto.Response.CalendarSessionResponse;
import com.proyecto.synapsevr.dto.Response.FilteredSessionsResponse;
import com.proyecto.synapsevr.Entity.SessionEntity;
import com.proyecto.synapsevr.Entity.PatientEntity;
import com.proyecto.synapsevr.Entity.UserEntity;
import com.proyecto.synapsevr.Repository.SessionRepository;
import com.proyecto.synapsevr.Repository.PatientRepository;
import com.proyecto.synapsevr.Repository.UserRepository;
import com.proyecto.synapsevr.Service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SessionServiceImpl implements SessionService {
    
    @Autowired
    private SessionRepository sessionRepository;
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public List<SessionResponse> getSessionsByPatientId(int patientId) {
        List<SessionEntity> sessions = sessionRepository.findByPatientPatientIdOrderBySessionDateDesc(patientId);
        return sessions.stream()
                .map(SessionResponse::new)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<SessionResponse> getTodaySessionByPatientId(int patientId) {
        Optional<SessionEntity> session = sessionRepository.findTodaySessionByPatientId(patientId);
        return session.map(SessionResponse::new);
    }
    
    @Override
    public Optional<SessionResponse> getLatestSessionByPatientId(int patientId) {
        Optional<SessionEntity> session = sessionRepository.findLatestSessionByPatientId(patientId);
        return session.map(SessionResponse::new);
    }
    
    @Override
    public SessionResponse createSession(SessionRequest createSession, String userEmail) {
        // Buscar el paciente
        PatientEntity patient = patientRepository.findById(createSession.getPatientId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        
        // Buscar el usuario por email (quien está logueado)
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Crear la sesión
        SessionEntity session = new SessionEntity();
        session.setSessionDate(createSession.getSessionDate());
        session.setDescription(createSession.getDescription());
        session.setPatient(patient);
        session.setUser(user);
        
        // Guardar en BD
        SessionEntity savedSession = sessionRepository.save(session);
        
        return new SessionResponse(savedSession);
    }
    
    @Override
    public boolean deleteSession(UUID sessionId, String userEmail) {
        Optional<SessionEntity> sessionOpt = sessionRepository.findById(sessionId);
        
        if (sessionOpt.isPresent()) {
            SessionEntity session = sessionOpt.get();
            
            // Verificar que la sesión pertenece al usuario logueado
            if (!session.getUser().getEmail().equals(userEmail)) {
                throw new RuntimeException("No tienes permisos para eliminar esta sesión");
            }
            
            sessionRepository.delete(session);
            return true;
        }
        
        return false;
    }
    
    @Override
    public Optional<SessionResponse> getSessionById(UUID sessionId) {
        Optional<SessionEntity> session = sessionRepository.findById(sessionId);
        return session.map(SessionResponse::new);
    }
    
    @Override
    public List<CalendarSessionResponse> getSessionsForCalendar(LocalDate startDate, LocalDate endDate) {
        List<SessionEntity> sessions = sessionRepository.findByDateRange(startDate, endDate);
        return sessions.stream()
                .map(CalendarSessionResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public FilteredSessionsResponse getSessionsWithFilters(Integer patientId, List<String> exposureLevels, 
                                                          LocalDate dateFrom, LocalDate dateTo, 
                                                          int page, int limit, String sortBy, String sortOrder) {
        // Convertir strings a enums
        List<SessionEntity.ExposureLevel> exposureLevelEnums = null;
        if (exposureLevels != null && !exposureLevels.isEmpty()) {
            exposureLevelEnums = exposureLevels.stream()
                    .map(level -> {
                        try {
                            return SessionEntity.ExposureLevel.valueOf(level.toUpperCase().replace(" ", "_"));
                        } catch (IllegalArgumentException e) {
                            // Si no se puede convertir, ignorar
                            return null;
                        }
                    })
                    .filter(level -> level != null)
                    .collect(Collectors.toList());
        }

        // Configurar paginación y ordenamiento
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(direction, sortBy));

        // Buscar sesiones con filtros
        Page<SessionEntity> sessionsPage = sessionRepository.findByPatientIdWithFilters(
                patientId, exposureLevelEnums, dateFrom, dateTo, pageable);

        // Mapear respuesta
        FilteredSessionsResponse response = new FilteredSessionsResponse();
        
        List<FilteredSessionsResponse.SessionSummary> sessionSummaries = sessionsPage.getContent().stream()
                .map(session -> {
                    FilteredSessionsResponse.SessionSummary summary = new FilteredSessionsResponse.SessionSummary();
                    summary.setId(session.getIdSession());
                    summary.setSessionDate(session.getSessionDate());
                    summary.setDescription(session.getDescription());
                    summary.setExposureLevel(session.getExposureLevel() != null ? session.getExposureLevel().getDisplayName() : null);
                    summary.setDuration(session.getDuration());
                    summary.setStatus(session.getStatus());
                    return summary;
                })
                .collect(Collectors.toList());
        
        response.setSessions(sessionSummaries);

        // Información de paginación
        FilteredSessionsResponse.PaginationInfo pagination = new FilteredSessionsResponse.PaginationInfo();
        pagination.setPage(page);
        pagination.setLimit(limit);
        pagination.setTotal(sessionsPage.getTotalElements());
        pagination.setTotalPages(sessionsPage.getTotalPages());
        response.setPagination(pagination);

        // Información de filtros aplicados
        FilteredSessionsResponse.FilterInfo filters = new FilteredSessionsResponse.FilterInfo();
        filters.setExposureLevel(exposureLevels);
        if (dateFrom != null || dateTo != null) {
            String dateRange = (dateFrom != null ? dateFrom.toString() : "inicio") + 
                              " to " + 
                              (dateTo != null ? dateTo.toString() : "fin");
            filters.setDateRange(dateRange);
        }
        response.setFilters(filters);

        return response;
    }

    @Override
    public SessionResponse updateSession(UUID sessionId, UpdateSessionRequest updateRequest, String userEmail) {
        // Buscar la sesión existente
        SessionEntity session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));
        
        // Verificar que la sesión pertenece al usuario logueado
        if (!session.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("No tienes permisos para editar esta sesión");
        }
        
        // Actualizar los campos permitidos
        if (updateRequest.getSessionDate() != null) {
            session.setSessionDate(updateRequest.getSessionDate());
        }
        
        if (updateRequest.getDescription() != null && !updateRequest.getDescription().trim().isEmpty()) {
            session.setDescription(updateRequest.getDescription());
        }
        
        // Guardar cambios
        SessionEntity updatedSession = sessionRepository.save(session);
        
        return new SessionResponse(updatedSession);
    }

}