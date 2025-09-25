package com.proyecto.synapsevr.Service.ServiceImpl;

import com.proyecto.synapsevr.Entity.MonitoringRecordEntity;
import com.proyecto.synapsevr.Repository.MonitoringRecordRepository;
import com.proyecto.synapsevr.Service.MonitoringService;
import com.proyecto.synapsevr.Service.MongoDbService;
import com.proyecto.synapsevr.dto.Request.MonitoringRecordRequest;
import com.proyecto.synapsevr.dto.Response.MonitoringRecordResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MonitoringServiceImpl implements MonitoringService {

    private final MonitoringRecordRepository monitoringRepository;
    private final MongoDbService mongoDbService;

    @Override
    @Transactional
    public MonitoringRecordResponse saveMonitoringRecord(MonitoringRecordRequest request, int userId) {
        log.info("Guardando registro de monitoreo para usuario: {} y paciente: {}", userId, request.getPatientId());
        
        try {
            // 1. Guardar los datos JSON en MongoDB
            String mongoDocumentId = mongoDbService.saveHeartRateData(request.getHeartRateRecords(), 
                                                                      request.getSessionId(), 
                                                                      request.getPatientId());
            
            // 2. Crear la entidad para PostgreSQL
            MonitoringRecordEntity entity = new MonitoringRecordEntity();
            entity.setSessionId(request.getSessionId());
            entity.setPatientId(request.getPatientId());
            entity.setUserId(userId);
            entity.setStartTime(request.getStartTime());
            entity.setEndTime(request.getEndTime());
            entity.setStartTimeLocal(request.getStartTimeLocal());
            entity.setEndTimeLocal(request.getEndTimeLocal());
            entity.setDuration(request.getDuration());
            entity.setTotalRecords(request.getTotalRecords());
            entity.setAvgHeartRate(request.getAvgHeartRate());
            entity.setMinHeartRate(request.getMinHeartRate());
            entity.setMaxHeartRate(request.getMaxHeartRate());
            entity.setMongoDocumentId(mongoDocumentId); // Referencia a MongoDB
            
            // 3. Guardar en PostgreSQL
            MonitoringRecordEntity savedEntity = monitoringRepository.save(entity);
            
            log.info("Registro de monitoreo guardado exitosamente con ID: {}", savedEntity.getMonitoringId());
            
            return new MonitoringRecordResponse(savedEntity);
            
        } catch (Exception e) {
            log.error("Error al guardar registro de monitoreo para usuario: {} y paciente: {}", userId, request.getPatientId(), e);
            throw new RuntimeException("Error al guardar el registro de monitoreo: " + e.getMessage());
        }
    }

    @Override
    public List<MonitoringRecordResponse> getMonitoringRecordsByPatient(int patientId, int userId) {
        log.info("Obteniendo registros de monitoreo para paciente: {} y usuario: {}", patientId, userId);
        
        List<MonitoringRecordEntity> entities = monitoringRepository.findByPatientIdAndUserId(patientId, userId);
        
        return entities.stream()
                .map(MonitoringRecordResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public MonitoringRecordResponse getMonitoringRecord(UUID monitoringId, int userId) {
        log.info("Obteniendo registro de monitoreo: {} para usuario: {}", monitoringId, userId);
        
        MonitoringRecordEntity entity = monitoringRepository.findByMonitoringIdAndUserId(monitoringId, userId)
                .orElseThrow(() -> new RuntimeException("Registro de monitoreo no encontrado o no pertenece al usuario"));
        
        return new MonitoringRecordResponse(entity);
    }

    @Override
    public List<MonitoringRecordResponse> getMonitoringRecordsByUser(int userId) {
        log.info("Obteniendo todos los registros de monitoreo para usuario: {}", userId);
        
        List<MonitoringRecordEntity> entities = monitoringRepository.findByUserId(userId);
        
        return entities.stream()
                .map(MonitoringRecordResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<MonitoringRecordResponse> getMonitoringRecordsBySession(UUID sessionId, int userId) {
        log.info("Obteniendo registros de monitoreo para sesión: {} y usuario: {}", sessionId, userId);
        
        List<MonitoringRecordEntity> entities = monitoringRepository.findBySessionId(sessionId);
        
        // Filtrar solo los que pertenecen al usuario actual (seguridad)
        List<MonitoringRecordEntity> filteredEntities = entities.stream()
                .filter(entity -> entity.getUserId().equals(userId))
                .collect(Collectors.toList());
        
        return filteredEntities.stream()
                .map(MonitoringRecordResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteMonitoringRecord(UUID monitoringId, int userId) {
        log.info("Eliminando registro de monitoreo: {} para usuario: {}", monitoringId, userId);
        
        try {
            // Buscar el registro y validar que pertenece al usuario
            MonitoringRecordEntity entity = monitoringRepository.findByMonitoringIdAndUserId(monitoringId, userId)
                    .orElseThrow(() -> new RuntimeException("Registro de monitoreo no encontrado o no pertenece al usuario"));
            
            // Eliminar datos de MongoDB si existen
            if (entity.getMongoDocumentId() != null) {
                try {
                    mongoDbService.deleteHeartRateData(entity.getMongoDocumentId());
                    log.info("Datos de MongoDB eliminados para documento: {}", entity.getMongoDocumentId());
                } catch (Exception e) {
                    log.warn("No se pudieron eliminar los datos de MongoDB: {}", e.getMessage());
                    // Continuamos con la eliminación en PostgreSQL aunque falle MongoDB
                }
            }
            
            // Eliminar registro de PostgreSQL
            monitoringRepository.delete(entity);
            
            log.info("Registro de monitoreo eliminado exitosamente: {}", monitoringId);
            
        } catch (Exception e) {
            log.error("Error al eliminar registro de monitoreo: {} para usuario: {}", monitoringId, userId, e);
            throw new RuntimeException("Error al eliminar el registro de monitoreo: " + e.getMessage());
        }
    }
}