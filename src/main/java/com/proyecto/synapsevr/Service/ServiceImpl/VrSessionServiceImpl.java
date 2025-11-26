package com.proyecto.synapsevr.Service.ServiceImpl;

import com.proyecto.synapsevr.Entity.SessionEntity;
import com.proyecto.synapsevr.Entity.VrSessionEntity;
import com.proyecto.synapsevr.Entity.UserEntity;
import com.proyecto.synapsevr.Entity.PatientEntity;
import com.proyecto.synapsevr.Repository.SessionRepository;
import com.proyecto.synapsevr.Repository.VrSessionRepository;
import com.proyecto.synapsevr.Repository.UserRepository;
import com.proyecto.synapsevr.Repository.PatientRepository;
import com.proyecto.synapsevr.Service.VrSessionService;
import com.proyecto.synapsevr.dto.Request.VrSessionRequest;
import com.proyecto.synapsevr.dto.Response.FilteredSessionsResponse;
import com.proyecto.synapsevr.dto.Response.VrSessionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VrSessionServiceImpl implements VrSessionService {

    private final SessionRepository sessionRepository;
    private final VrSessionRepository vrSessionRepository;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;

    @Override
    @Transactional
    public VrSessionResponse createVrSession(VrSessionRequest request, String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        PatientEntity patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        // Crear sesión regular primero
        SessionEntity session = new SessionEntity();
        session.setSessionDate(request.getSessionDate());
        session.setDescription(request.getDescription());
        session.setDuration(request.getDuration());
        session.setStatus(request.getStatus());
        session.setUser(user);
        session.setPatient(patient);
        
        // Convertir exposure level
        try {
            SessionEntity.ExposureLevel exposureLevel = SessionEntity.ExposureLevel.valueOf(
                request.getExposureLevel().toUpperCase().replace(" ", "_"));
            session.setExposureLevel(exposureLevel);
        } catch (IllegalArgumentException e) {
            session.setExposureLevel(SessionEntity.ExposureLevel.MEDIO);
        }

        session = sessionRepository.save(session);

        // Crear sesión VR
        VrSessionEntity vrSession = new VrSessionEntity();
        vrSession.setSessionId(session.getIdSession());
        vrSession.setVrScenario(request.getVrScenario());
        vrSession.setVrDevice(request.getVrDevice());
        vrSession.setImmersionDuration(request.getImmersionDuration());
        vrSession.setMovementTrackingData(request.getMovementTrackingData());
        vrSession.setEnvironmentSettings(request.getEnvironmentSettings());

        vrSession = vrSessionRepository.save(vrSession);

        return mapToVrSessionResponse(session, vrSession);
    }

    @Override
    public FilteredSessionsResponse getVrSessionsByPatient(Integer patientId, String vrScenario, 
                                                          LocalDate dateFrom, LocalDate dateTo, 
                                                          int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "sessionDate"));
        
        Page<SessionEntity> sessionsPage = sessionRepository.findVrSessionsByPatientIdWithFilters(
                patientId, vrScenario, dateFrom, dateTo, pageable);

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

        return response;
    }

    @Override
    public List<VrSessionResponse> getVrSessionsByScenario(String scenario) {
        List<VrSessionEntity> vrSessions = vrSessionRepository.findByVrScenario(scenario);
        return vrSessions.stream()
                .map(vrSession -> {
                    SessionEntity session = sessionRepository.findById(vrSession.getSessionId()).orElse(null);
                    return mapToVrSessionResponse(session, vrSession);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<VrSessionResponse> getVrSessionsByDevice(String device) {
        List<VrSessionEntity> vrSessions = vrSessionRepository.findByVrDevice(device);
        return vrSessions.stream()
                .map(vrSession -> {
                    SessionEntity session = sessionRepository.findById(vrSession.getSessionId()).orElse(null);
                    return mapToVrSessionResponse(session, vrSession);
                })
                .collect(Collectors.toList());
    }

    private VrSessionResponse mapToVrSessionResponse(SessionEntity session, VrSessionEntity vrSession) {
        VrSessionResponse response = new VrSessionResponse();
        
        if (session != null) {
            response.setSessionId(session.getIdSession());
            response.setSessionDate(session.getSessionDate());
            response.setDescription(session.getDescription());
            response.setExposureLevel(session.getExposureLevel() != null ? session.getExposureLevel().getDisplayName() : null);
            response.setDuration(session.getDuration());
            response.setStatus(session.getStatus());
            
            if (session.getPatient() != null) {
                response.setPatientId(session.getPatient().getPatientId());
                response.setPatientName(session.getPatient().getPatientName());
            }
        }
        
        if (vrSession != null) {
            response.setVrSessionId(vrSession.getId());
            response.setVrScenario(vrSession.getVrScenario());
            response.setVrDevice(vrSession.getVrDevice());
            response.setImmersionDuration(vrSession.getImmersionDuration());
            response.setMovementTrackingData(vrSession.getMovementTrackingData());
            response.setEnvironmentSettings(vrSession.getEnvironmentSettings());
        }
        
        return response;
    }
}