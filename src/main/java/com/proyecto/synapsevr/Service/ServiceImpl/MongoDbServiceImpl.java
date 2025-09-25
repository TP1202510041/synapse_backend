package com.proyecto.synapsevr.Service.ServiceImpl;

import com.proyecto.synapsevr.Service.MongoDbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MongoDbServiceImpl implements MongoDbService {

    private final MongoTemplate mongoTemplate;

    @PostConstruct
    public void testConnection() {
        try {
            mongoTemplate.getCollectionNames();
            log.info("✅ Conexión exitosa a MongoDB Atlas");
        } catch (Exception e) {
            log.error("❌ Error conectando a MongoDB Atlas: {}", e.getMessage());
        }
    }

    @Override
    public String saveHeartRateData(List<Object> heartRateRecords, UUID sessionId, Integer patientId) {
        try {
            log.info("Guardando datos de heartRate en MongoDB para sesión: {}", sessionId);
            
            // Crear documento para MongoDB
            HeartRateDocument document = new HeartRateDocument();
            document.setId(UUID.randomUUID().toString());
            document.setSessionId(sessionId.toString());
            document.setPatientId(patientId);
            document.setHeartRateRecords(heartRateRecords);
            document.setCreatedAt(LocalDateTime.now());
            document.setTotalRecords(heartRateRecords.size());
            
            // Guardar en MongoDB
            HeartRateDocument savedDocument = mongoTemplate.save(document, "heart_rate_data");
            
            log.info("Datos guardados en MongoDB con ID: {}", savedDocument.getId());
            return savedDocument.getId();
            
        } catch (Exception e) {
            log.error("Error al guardar datos en MongoDB para sesión: {}", sessionId, e);
            throw new RuntimeException("Error al guardar datos en MongoDB: " + e.getMessage());
        }
    }

    @Override
    public void deleteHeartRateData(String mongoDocumentId) {
        try {
            log.info("Eliminando documento de MongoDB con ID: {}", mongoDocumentId);
            
            Query query = new Query(Criteria.where("id").is(mongoDocumentId));
            mongoTemplate.remove(query, HeartRateDocument.class, "heart_rate_data");
            
            log.info("Documento eliminado de MongoDB: {}", mongoDocumentId);
            
        } catch (Exception e) {
            log.error("Error al eliminar documento de MongoDB: {}", mongoDocumentId, e);
            throw new RuntimeException("Error al eliminar datos de MongoDB: " + e.getMessage());
        }
    }

    @Override
    public HeartRateDocument getHeartRateData(String mongoDocumentId) {
        try {
            log.info("Obteniendo documento de MongoDB con ID: {}", mongoDocumentId);
            
            Query query = new Query(Criteria.where("id").is(mongoDocumentId));
            HeartRateDocument document = mongoTemplate.findOne(query, HeartRateDocument.class, "heart_rate_data");
            
            if (document == null) {
                log.warn("Documento no encontrado en MongoDB: {}", mongoDocumentId);
                throw new RuntimeException("Documento no encontrado en MongoDB");
            }
            
            return document;
            
        } catch (Exception e) {
            log.error("Error al obtener documento de MongoDB: {}", mongoDocumentId, e);
            throw new RuntimeException("Error al obtener datos de MongoDB: " + e.getMessage());
        }
    }

    // Clase para el documento de MongoDB
    public static class HeartRateDocument {
        private String id;
        private String sessionId;
        private Integer patientId;
        private List<Object> heartRateRecords;
        private LocalDateTime createdAt;
        private Integer totalRecords;

        // Constructores
        public HeartRateDocument() {}

        public HeartRateDocument(String id, String sessionId, Integer patientId, 
                               List<Object> heartRateRecords, LocalDateTime createdAt, Integer totalRecords) {
            this.id = id;
            this.sessionId = sessionId;
            this.patientId = patientId;
            this.heartRateRecords = heartRateRecords;
            this.createdAt = createdAt;
            this.totalRecords = totalRecords;
        }

        // Getters y Setters
        public String getId() { 
            return id; 
        }
        
        public void setId(String id) { 
            this.id = id; 
        }

        public String getSessionId() { 
            return sessionId; 
        }
        
        public void setSessionId(String sessionId) { 
            this.sessionId = sessionId; 
        }

        public Integer getPatientId() { 
            return patientId; 
        }
        
        public void setPatientId(Integer patientId) { 
            this.patientId = patientId; 
        }

        public List<Object> getHeartRateRecords() { 
            return heartRateRecords; 
        }
        
        public void setHeartRateRecords(List<Object> heartRateRecords) { 
            this.heartRateRecords = heartRateRecords; 
        }

        public LocalDateTime getCreatedAt() { 
            return createdAt; 
        }
        
        public void setCreatedAt(LocalDateTime createdAt) { 
            this.createdAt = createdAt; 
        }

        public Integer getTotalRecords() { 
            return totalRecords; 
        }
        
        public void setTotalRecords(Integer totalRecords) { 
            this.totalRecords = totalRecords; 
        }
    }
}