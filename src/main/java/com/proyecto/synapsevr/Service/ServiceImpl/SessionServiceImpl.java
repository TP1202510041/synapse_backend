package com.proyecto.synapsevr.Service.ServiceImpl;

import com.proyecto.synapsevr.dto.Request.SessionRequest;
import com.proyecto.synapsevr.dto.Response.SessionResponse;
import com.proyecto.synapsevr.dto.Response.CalendarSessionResponse;
import com.proyecto.synapsevr.Entity.SessionEntity;
import com.proyecto.synapsevr.Entity.PatientEntity;
import com.proyecto.synapsevr.Entity.UserEntity;
import com.proyecto.synapsevr.Repository.SessionRepository;
import com.proyecto.synapsevr.Repository.PatientRepository;
import com.proyecto.synapsevr.Repository.UserRepository;
import com.proyecto.synapsevr.Service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
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

}