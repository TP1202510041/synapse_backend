-- Crear tabla para observaciones de monitoreo
CREATE TABLE IF NOT EXISTS monitoring_observations (
    id BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    monitoring_id BINARY(16) NOT NULL,
    patient_id INT NOT NULL,
    therapist_id INT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT DEFAULT 1,
    is_deleted BOOLEAN DEFAULT FALSE,
    
    -- Índices para mejorar rendimiento
    INDEX idx_monitoring_observations_monitoring_id (monitoring_id),
    INDEX idx_monitoring_observations_patient_id (patient_id),
    INDEX idx_monitoring_observations_therapist_id (therapist_id),
    INDEX idx_monitoring_observations_created_at (created_at),
    INDEX idx_monitoring_observations_is_deleted (is_deleted),
    
    -- Claves foráneas (opcional, para integridad referencial)
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (therapist_id) REFERENCES users(user_id) ON DELETE CASCADE
    -- Nota: monitoring_id referencia a monitoringrecords.monitoring_id pero no agregamos FK 
    -- porque puede causar problemas de dependencias circulares
);

-- Comentarios para documentación
ALTER TABLE monitoring_observations COMMENT = 'Observaciones clínicas asociadas a registros de monitoreo específicos';
ALTER TABLE monitoring_observations MODIFY COLUMN monitoring_id BINARY(16) COMMENT 'ID del registro de monitoreo asociado';
ALTER TABLE monitoring_observations MODIFY COLUMN patient_id INT COMMENT 'ID del paciente (debe coincidir con el del monitoreo)';
ALTER TABLE monitoring_observations MODIFY COLUMN therapist_id INT COMMENT 'ID del terapeuta que hace la observación';
ALTER TABLE monitoring_observations MODIFY COLUMN content TEXT COMMENT 'Contenido de la observación clínica (10-2000 caracteres)';
ALTER TABLE monitoring_observations MODIFY COLUMN version INT COMMENT 'Versión de la observación para control de concurrencia';
ALTER TABLE monitoring_observations MODIFY COLUMN is_deleted BOOLEAN COMMENT 'Soft delete flag';