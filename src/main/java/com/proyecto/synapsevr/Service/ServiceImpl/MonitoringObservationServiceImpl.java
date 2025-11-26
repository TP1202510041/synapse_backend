package com.proyecto.synapsevr.Service.ServiceImpl;

import com.proyecto.synapsevr.Entity.MonitoringObservationEntity;
import com.proyecto.synapsevr.Entity.MonitoringRecordEntity;
import com.proyecto.synapsevr.Entity.PatientEntity;
import com.proyecto.synapsevr.Entity.UserEntity;
import com.proyecto.synapsevr.Entity.AuditLogEntity;
import com.proyecto.synapsevr.Repository.MonitoringObservationRepository;
import com.proyecto.synapsevr.Repository.MonitoringRecordRepository;
import com.proyecto.synapsevr.Repository.UserRepository;
import com.proyecto.synapsevr.Repository.PatientRepository;
import com.proyecto.synapsevr.Service.MonitoringObservationService;
import com.proyecto.synapsevr.Service.AuditService;
import com.proyecto.synapsevr.dto.Request.MonitoringObservationRequest;
import com.proyecto.synapsevr.dto.Response.MonitoringObservationResponse;
import com.proyecto.synapsevr.dto.Response.PaginatedMonitoringObservationsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonitoringObservationServiceImpl implements MonitoringObservationService {

    private final MonitoringObservationRepository observationRepository;
    private final MonitoringRecordRepository monitoringRepository;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final AuditService auditService;

    @Override
    @Transactional
    public MonitoringObservationResponse createMonitoringObservation(MonitoringObservationRequest request, String userEmail) {
        System.out.println("🔍 [MONITORING-OBS] Iniciando creación de observación de monitoreo...");
        System.out.println("📧 Email del usuario: " + userEmail);
        System.out.println("🆔 MonitoringId: " + request.getMonitoringId());
        System.out.println("👤 PatientId solicitado: " + request.getPatientId());
        System.out.println("📝 Contenido: " + request.getContent());

        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + userEmail));
        
        System.out.println("✅ Usuario encontrado: ID=" + user.getUserId() + ", Nombre=" + user.getUserName());

        // Validar que el registro de monitoreo existe y pertenece al paciente correcto
        MonitoringRecordEntity monitoringRecord = monitoringRepository.findById(request.getMonitoringId())
                .orElseThrow(() -> new RuntimeException("Registro de monitoreo no encontrado: " + request.getMonitoringId()));
        
        System.out.println("✅ Registro de monitoreo encontrado");
        System.out.println("🔍 PatientId del monitoreo: " + monitoringRecord.getPatientId());
        
        // Validar que el paciente del monitoreo coincida con el solicitado
        if (!monitoringRecord.getPatientId().equals(request.getPatientId())) {
            System.err.println("❌ Paciente incorrecto. Monitoreo pertenece al paciente " + monitoringRecord.getPatientId() + 
                             " pero se solicitó para paciente " + request.getPatientId());
            throw new RuntimeException("El monitoreo " + request.getMonitoringId() + 
                                     " pertenece al paciente " + monitoringRecord.getPatientId() + 
                                     ", no al paciente " + request.getPatientId());
        }
        
        System.out.println("✅ Validación de monitoreo-paciente correcta");

        // SIEMPRE crear nueva observación (múltiples permitidas por monitoreo)
        System.out.println("🆕 Creando nueva observación de monitoreo (múltiples permitidas)...");
        
        MonitoringObservationEntity observation = new MonitoringObservationEntity();
        observation.setMonitoringId(request.getMonitoringId());
        observation.setPatientId(monitoringRecord.getPatientId()); // Usar el ID correcto del monitoreo
        observation.setTherapistId(user.getUserId());
        observation.setContent(request.getContent());
        observation.setCreatedAt(LocalDateTime.now());
        observation.setUpdatedAt(LocalDateTime.now());
        observation.setVersion(1);
        observation.setIsDeleted(false);

        // Guardar la observación
        try {
            System.out.println("💾 Guardando observación de monitoreo en base de datos...");
            observation = observationRepository.save(observation);
            System.out.println("✅ Observación de monitoreo guardada exitosamente con ID: " + observation.getId());
            System.out.println("📊 Versión: " + observation.getVersion());
            System.out.println("👤 Paciente correcto: " + observation.getPatientId());
            System.out.println("🆔 Monitoreo: " + observation.getMonitoringId());
            System.out.println("👨‍⚕️ Terapeuta: " + observation.getTherapistId());
            System.out.println("🕐 Creado: " + observation.getCreatedAt());
        } catch (Exception e) {
            System.err.println("❌ Error guardando observación de monitoreo: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error guardando observación de monitoreo: " + e.getMessage(), e);
        }
        
        // Auditoría (no crítica)
        try {
            auditService.logAction(user.getUserId(), "MonitoringObservation", observation.getId().toString(),
                    null, null, request.getContent(), AuditLogEntity.AuditAction.CREATE, null, null);
            System.out.println("📋 Auditoría registrada correctamente");
        } catch (Exception e) {
            System.err.println("⚠️ Error en auditoría (no crítico): " + e.getMessage());
        }
        
        MonitoringObservationResponse response = mapToResponse(observation);
        System.out.println("🎯 Respuesta generada: " + response.getId());
        return response;
    }

    @Override
    public List<MonitoringObservationResponse> getObservationsByMonitoringId(UUID monitoringId) {
        System.out.println("🔍 [MONITORING-OBS] Buscando observaciones para monitoreo: " + monitoringId);
        
        List<MonitoringObservationEntity> observations = observationRepository.findByMonitoringIdAndIsDeletedFalse(monitoringId);
        
        System.out.println("📊 Observaciones de monitoreo encontradas: " + observations.size());
        for (int i = 0; i < observations.size(); i++) {
            MonitoringObservationEntity obs = observations.get(i);
            System.out.println("  " + (i+1) + ". ID: " + obs.getId() + 
                             ", Terapeuta: " + obs.getTherapistId() + 
                             ", Contenido: " + obs.getContent().substring(0, Math.min(50, obs.getContent().length())) + "...");
        }
        
        List<MonitoringObservationResponse> responses = observations.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
                
        System.out.println("✅ Respuestas generadas: " + responses.size());
        return responses;
    }

    @Override
    public PaginatedMonitoringObservationsResponse getObservationsByPatient(Integer patientId, int page, int limit, String sortBy, String sortOrder) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(direction, sortBy));

        Page<MonitoringObservationEntity> observationsPage = observationRepository.findByPatientIdAndIsDeletedFalse(patientId, pageable);

        PaginatedMonitoringObservationsResponse response = new PaginatedMonitoringObservationsResponse();
        response.setObservations(observationsPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList()));

        PaginatedMonitoringObservationsResponse.PaginationInfo pagination = new PaginatedMonitoringObservationsResponse.PaginationInfo();
        pagination.setPage(page);
        pagination.setLimit(limit);
        pagination.setTotal(observationsPage.getTotalElements());
        pagination.setTotalPages(observationsPage.getTotalPages());
        response.setPagination(pagination);

        return response;
    }

    @Override
    @Transactional
    public MonitoringObservationResponse updateMonitoringObservation(UUID observationId, MonitoringObservationRequest request, String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        MonitoringObservationEntity observation = observationRepository.findById(observationId)
                .orElseThrow(() -> new RuntimeException("Observación de monitoreo no encontrada"));

        if (!observation.getTherapistId().equals(user.getUserId())) {
            throw new RuntimeException("Solo el autor puede editar su observación de monitoreo");
        }

        String oldContent = observation.getContent();
        observation.setContent(request.getContent());
        observation.setUpdatedAt(LocalDateTime.now());
        observation.setVersion(observation.getVersion() + 1);

        observation = observationRepository.save(observation);

        // Auditoría
        auditService.logAction(user.getUserId(), "MonitoringObservation", observation.getId().toString(),
                "content", oldContent, request.getContent(), AuditLogEntity.AuditAction.UPDATE, null, null);

        return mapToResponse(observation);
    }

    @Override
    @Transactional
    public void deleteMonitoringObservation(UUID observationId, String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        MonitoringObservationEntity observation = observationRepository.findById(observationId)
                .orElseThrow(() -> new RuntimeException("Observación de monitoreo no encontrada"));

        if (!observation.getTherapistId().equals(user.getUserId()) && !user.getRole().equals(UserEntity.Role.ADMIN)) {
            throw new RuntimeException("Solo el autor o un administrador puede eliminar la observación de monitoreo");
        }

        // Soft delete
        observation.setIsDeleted(true);
        observation.setUpdatedAt(LocalDateTime.now());
        observationRepository.save(observation);

        // Auditoría
        auditService.logAction(user.getUserId(), "MonitoringObservation", observation.getId().toString(),
                null, null, null, AuditLogEntity.AuditAction.DELETE, null, null);
    }

    @Override
    public List<MonitoringObservationResponse> getObservationsByTherapist(String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<MonitoringObservationEntity> observations = observationRepository.findByTherapistIdAndIsDeletedFalse(user.getUserId());
        
        return observations.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private MonitoringObservationResponse mapToResponse(MonitoringObservationEntity observation) {
        MonitoringObservationResponse response = new MonitoringObservationResponse();
        response.setId(observation.getId());
        response.setMonitoringId(observation.getMonitoringId());
        response.setPatientId(observation.getPatientId());
        response.setTherapistId(observation.getTherapistId());
        response.setContent(observation.getContent());
        response.setCreatedAt(observation.getCreatedAt());
        response.setUpdatedAt(observation.getUpdatedAt());
        response.setVersion(observation.getVersion());

        // Obtener nombres reales
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

            // Obtener información del monitoreo
            MonitoringRecordEntity monitoringRecord = monitoringRepository.findById(observation.getMonitoringId()).orElse(null);
            if (monitoringRecord != null) {
                MonitoringObservationResponse.MonitoringInfo monitoringInfo = new MonitoringObservationResponse.MonitoringInfo();
                monitoringInfo.setDuration(monitoringRecord.getDuration());
                monitoringInfo.setAvgHeartRate(monitoringRecord.getAvgHeartRate() != null ? monitoringRecord.getAvgHeartRate().doubleValue() : null);
                monitoringInfo.setMaxHeartRate(monitoringRecord.getMaxHeartRate() != null ? monitoringRecord.getMaxHeartRate().doubleValue() : null);
                monitoringInfo.setMonitoringCreatedAt(monitoringRecord.getCreatedAt());
                response.setMonitoringInfo(monitoringInfo);
            }
        } catch (Exception e) {
            // Fallback a placeholders si hay error
            response.setTherapistName("Terapeuta " + observation.getTherapistId());
            response.setPatientName("Paciente " + observation.getPatientId());
        }

        return response;
    }
}