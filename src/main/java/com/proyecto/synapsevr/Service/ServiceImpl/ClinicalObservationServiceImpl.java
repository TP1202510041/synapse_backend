package com.proyecto.synapsevr.Service.ServiceImpl;

import com.proyecto.synapsevr.Entity.ClinicalObservationEntity;
import com.proyecto.synapsevr.Entity.PatientEntity;
import com.proyecto.synapsevr.Entity.UserEntity;
import com.proyecto.synapsevr.Repository.ClinicalObservationRepository;
import com.proyecto.synapsevr.Repository.UserRepository;
import com.proyecto.synapsevr.Service.ClinicalObservationService;
import com.proyecto.synapsevr.Service.AuditService;
import com.proyecto.synapsevr.dto.Request.ClinicalObservationRequest;
import com.proyecto.synapsevr.dto.Request.UpdateObservationRequest;
import com.proyecto.synapsevr.dto.Response.ClinicalObservationResponse;
import com.proyecto.synapsevr.dto.Response.PaginatedObservationsResponse;
import com.proyecto.synapsevr.Entity.AuditLogEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClinicalObservationServiceImpl implements ClinicalObservationService {

    private final ClinicalObservationRepository observationRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final com.proyecto.synapsevr.Repository.PatientRepository patientRepository;
    private final com.proyecto.synapsevr.Repository.SessionRepository sessionRepository;

    @Override
    @Transactional
    public ClinicalObservationResponse createObservation(ClinicalObservationRequest request, String userEmail) {
        System.out.println("🔍 [OBSERVACIONES] Iniciando creación de observación...");
        System.out.println("📧 Email del usuario: " + userEmail);
        System.out.println("🆔 SessionId: " + request.getSessionId());
        System.out.println("👤 PatientId: " + request.getPatientId());
        System.out.println("📝 Contenido: " + request.getContent());

        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + userEmail));

        System.out.println("✅ Usuario encontrado: ID=" + user.getUserId() + ", Nombre=" + user.getUserName());

        // ← CAMBIO: Validar que la sesión existe y pertenece al paciente correcto
        var session = sessionRepository.findById(request.getSessionId()).orElse(null);
        
        if (session == null) {
            System.err.println("❌ Sesión no encontrada: " + request.getSessionId());
            throw new RuntimeException("Sesión no encontrada: " + request.getSessionId());
        }
        
        // Validar que el paciente de la sesión coincida con el solicitado
        Integer sessionPatientId = session.getPatient().getPatientId();
        System.out.println("🔍 PatientId de la sesión: " + sessionPatientId);
        System.out.println("🔍 PatientId solicitado: " + request.getPatientId());
        
        if (!sessionPatientId.equals(request.getPatientId())) {
            System.err.println("❌ Paciente incorrecto. Sesión pertenece al paciente " + sessionPatientId + 
                             " pero se solicitó para paciente " + request.getPatientId());
            throw new RuntimeException("La sesión " + request.getSessionId() + 
                                     " pertenece al paciente " + sessionPatientId + 
                                     ", no al paciente " + request.getPatientId());
        }
        
        System.out.println("✅ Validación de sesión-paciente correcta");

        // ← CAMBIO: SIEMPRE crear nueva observación (no más upsert)
        System.out.println("🆕 Creando nueva observación (múltiples permitidas)...");
        
        ClinicalObservationEntity observation = new ClinicalObservationEntity();
        observation.setSessionId(request.getSessionId());
        observation.setPatientId(sessionPatientId); // ← Usar el ID correcto de la sesión
        observation.setTherapistId(user.getUserId());
        observation.setContent(request.getContent());
        observation.setCreatedAt(LocalDateTime.now());
        observation.setUpdatedAt(LocalDateTime.now());
        observation.setVersion(1);
        observation.setIsDeleted(false);

        // Guardar la observación
        try {
            System.out.println("💾 Guardando observación en base de datos...");
            observation = observationRepository.save(observation);
            System.out.println("✅ Observación guardada exitosamente con ID: " + observation.getId());
            System.out.println("📊 Versión: " + observation.getVersion());
            System.out.println("👤 Paciente correcto: " + observation.getPatientId());
            System.out.println("🆔 Sesión: " + observation.getSessionId());
            System.out.println("👨‍⚕️ Terapeuta: " + observation.getTherapistId());
            System.out.println("🕐 Creado: " + observation.getCreatedAt());
        } catch (Exception e) {
            System.err.println("❌ Error guardando observación: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error guardando observación: " + e.getMessage(), e);
        }

        // Auditoría (no crítica)
        try {
            auditService.logAction(user.getUserId(), "ClinicalObservation", observation.getId().toString(),
                    null, null, request.getContent(), AuditLogEntity.AuditAction.CREATE, null, null);
            System.out.println("📋 Auditoría registrada correctamente");
        } catch (Exception e) {
            System.err.println("⚠️ Error en auditoría (no crítico): " + e.getMessage());
            // No lanzamos excepción aquí porque la auditoría no es crítica
        }

        ClinicalObservationResponse response = mapToResponse(observation);
        System.out.println("🎯 Respuesta generada: " + response.getId());
        return response;
    }

    @Override
    @Transactional
    public ClinicalObservationResponse updateObservation(UUID observationId, UpdateObservationRequest request,
            String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        ClinicalObservationEntity observation = observationRepository.findById(observationId)
                .orElseThrow(() -> new RuntimeException("Observación no encontrada"));

        if (!observation.getTherapistId().equals(user.getUserId())) {
            throw new RuntimeException("Solo el autor puede editar su observación");
        }

        if (!observation.getVersion().equals(request.getVersion())) {
            throw new RuntimeException("Conflicto de versión - la observación fue modificada por otro usuario");
        }

        String oldContent = observation.getContent();
        observation.setContent(request.getContent());
        observation.setUpdatedAt(LocalDateTime.now());
        observation.setVersion(observation.getVersion() + 1);

        observation = observationRepository.save(observation);

        // Auditoría
        auditService.logAction(user.getUserId(), "ClinicalObservation", observation.getId().toString(),
                "content", oldContent, request.getContent(), AuditLogEntity.AuditAction.UPDATE, null, null);

        return mapToResponse(observation);
    }

    @Override
    public List<ClinicalObservationResponse> getObservationsBySession(UUID sessionId) {
        System.out.println("🔍 [OBSERVACIONES] Buscando observaciones para sesión: " + sessionId);

        List<ClinicalObservationEntity> observations = observationRepository
                .findBySessionIdAndIsDeletedFalse(sessionId);

        System.out.println("📊 Observaciones encontradas: " + observations.size());
        for (int i = 0; i < observations.size(); i++) {
            ClinicalObservationEntity obs = observations.get(i);
            System.out.println("  " + (i + 1) + ". ID: " + obs.getId() +
                    ", Terapeuta: " + obs.getTherapistId() +
                    ", Contenido: " + obs.getContent().substring(0, Math.min(50, obs.getContent().length())) + "...");
        }

        List<ClinicalObservationResponse> responses = observations.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        System.out.println("✅ Respuestas generadas: " + responses.size());
        return responses;
    }

    @Override
    public PaginatedObservationsResponse getObservationsByPatient(Integer patientId, int page, int limit, String sortBy,
            String sortOrder) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(direction, sortBy));

        Page<ClinicalObservationEntity> observationsPage = observationRepository
                .findByPatientIdAndIsDeletedFalse(patientId, pageable);

        PaginatedObservationsResponse response = new PaginatedObservationsResponse();
        response.setObservations(observationsPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList()));

        PaginatedObservationsResponse.PaginationInfo pagination = new PaginatedObservationsResponse.PaginationInfo();
        pagination.setPage(page);
        pagination.setLimit(limit);
        pagination.setTotal(observationsPage.getTotalElements());
        pagination.setTotalPages(observationsPage.getTotalPages());
        response.setPagination(pagination);

        return response;
    }

    @Override
    @Transactional
    public void deleteObservation(UUID observationId, String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        ClinicalObservationEntity observation = observationRepository.findById(observationId)
                .orElseThrow(() -> new RuntimeException("Observación no encontrada"));

        if (!observation.getTherapistId().equals(user.getUserId()) && !user.getRole().equals(UserEntity.Role.ADMIN)) {
            throw new RuntimeException("Solo el autor o un administrador puede eliminar la observación");
        }

        // Soft delete
        observation.setIsDeleted(true);
        observation.setUpdatedAt(LocalDateTime.now());
        observationRepository.save(observation);

        // Auditoría
        auditService.logAction(user.getUserId(), "ClinicalObservation", observation.getId().toString(),
                null, null, null, AuditLogEntity.AuditAction.DELETE, null, null);
    }

    private ClinicalObservationResponse mapToResponse(ClinicalObservationEntity observation) {
        ClinicalObservationResponse response = new ClinicalObservationResponse();
        response.setId(observation.getId());
        response.setSessionId(observation.getSessionId());
        response.setPatientId(observation.getPatientId());
        response.setTherapistId(observation.getTherapistId());
        response.setContent(observation.getContent());
        response.setCreatedAt(observation.getCreatedAt());
        response.setUpdatedAt(observation.getUpdatedAt());
        response.setVersion(observation.getVersion());

        // ← ARREGLADO: Obtener nombres reales
        try {
            UserEntity therapist = userRepository.findById(observation.getTherapistId()).orElse(null);
            if (therapist != null) {
                response.setTherapistName(therapist.getUserName());
            } else {
                response.setTherapistName("Terapeuta " + observation.getTherapistId());
            }

            PatientEntity patient = patientRepository.findById(observation.getPatientId()).orElse(null);
            if (patient != null) {
                response.setPatientName(patient.getPatientName());
            } else {
                response.setPatientName("Paciente " + observation.getPatientId());
            }
        } catch (Exception e) {
            // Fallback a placeholders si hay error
            response.setTherapistName("Terapeuta " + observation.getTherapistId());
            response.setPatientName("Paciente " + observation.getPatientId());
        }

        return response;
    }
}