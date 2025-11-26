-- Script SQL para crear las nuevas tablas requeridas por las User Stories

-- 1. Agregar columnas a la tabla sessions existente
ALTER TABLE sessions 
ADD COLUMN exposure_level ENUM('BAJO', 'MEDIO', 'ALTO', 'MUY_ALTO') DEFAULT 'MEDIO',
ADD COLUMN duration INT,
ADD COLUMN status VARCHAR(50);

-- Crear índices para la tabla sessions
CREATE INDEX idx_sessions_exposure_level ON sessions(exposure_level);
CREATE INDEX idx_sessions_patient_exposure ON sessions(patient_id_fk, exposure_level);

-- 2. Tabla de observaciones clínicas (HU-004-004)
CREATE TABLE clinical_observations (
    id VARCHAR(36) PRIMARY KEY,
    session_id VARCHAR(36) NOT NULL,
    patient_id INT NOT NULL,
    therapist_id INT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT DEFAULT 1,
    is_deleted BOOLEAN DEFAULT FALSE,
    INDEX idx_session_observations (session_id),
    INDEX idx_patient_observations (patient_id),
    INDEX idx_therapist_observations (therapist_id),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    FOREIGN KEY (therapist_id) REFERENCES users(user_id)
);

-- 3. Tabla de auditoría (HU-012-012)
CREATE TABLE audit_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    entity_type VARCHAR(50) NOT NULL,
    entity_id VARCHAR(36) NOT NULL,
    field_name VARCHAR(100),
    old_value TEXT,
    new_value TEXT,
    action ENUM('CREATE', 'UPDATE', 'DELETE') NOT NULL,
    ip_address VARCHAR(45),
    user_agent TEXT,
    INDEX idx_audit_user (user_id),
    INDEX idx_audit_entity (entity_type, entity_id),
    INDEX idx_audit_timestamp (timestamp)
);

-- 4. Tabla de intentos de login (HU-020-020)
CREATE TABLE login_attempts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    username VARCHAR(100),
    ip_address VARCHAR(45) NOT NULL,
    user_agent TEXT,
    attempt_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    success BOOLEAN NOT NULL,
    failure_reason VARCHAR(100),
    INDEX idx_attempts_user (user_id),
    INDEX idx_attempts_ip (ip_address),
    INDEX idx_attempts_time (attempt_time)
);

-- 5. Tabla de bloqueos de cuenta (HU-020-020)
CREATE TABLE account_lockouts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    ip_address VARCHAR(45),
    locked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    unlock_at TIMESTAMP NOT NULL,
    reason VARCHAR(100),
    unlocked_by INT,
    INDEX idx_lockouts_user (user_id),
    INDEX idx_lockouts_ip (ip_address)
);

-- 6. Tabla de tokens de recuperación de contraseña (HU-024-024)
CREATE TABLE password_reset_tokens (
    id VARCHAR(36) PRIMARY KEY,
    user_id INT NOT NULL,
    token VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    used_at TIMESTAMP NULL,
    ip_address VARCHAR(45),
    INDEX idx_reset_token (token),
    INDEX idx_reset_user (user_id),
    INDEX idx_reset_expires (expires_at),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 7. Tabla de logs de exportación (HU-011-011)
CREATE TABLE export_logs (
    id VARCHAR(36) PRIMARY KEY,
    patient_id INT NOT NULL,
    therapist_id INT NOT NULL,
    export_type VARCHAR(50) NOT NULL,
    file_path VARCHAR(500),
    status ENUM('PROCESSING', 'COMPLETED', 'FAILED') DEFAULT 'PROCESSING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    INDEX idx_export_patient (patient_id),
    INDEX idx_export_therapist (therapist_id),
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    FOREIGN KEY (therapist_id) REFERENCES users(user_id)
);

-- 8. Tabla de sesiones VR (HU-032-032 y HU-033-033)
CREATE TABLE vr_sessions (
    id VARCHAR(36) PRIMARY KEY,
    session_id VARCHAR(36) NOT NULL,
    vr_scenario VARCHAR(100) NOT NULL,
    vr_device VARCHAR(100),
    immersion_duration INT,
    movement_tracking_data JSON,
    environment_settings JSON,
    INDEX idx_vr_scenario (vr_scenario),
    INDEX idx_vr_device (vr_device),
    FOREIGN KEY (session_id) REFERENCES sessions(id_session)
);

-- Insertar datos de ejemplo para testing
INSERT INTO clinical_observations (id, session_id, patient_id, therapist_id, content, created_at, updated_at, version, is_deleted) 
SELECT 
    UUID() as id,
    s.id_session,
    s.patient_id_fk,
    s.user_id_fk,
    'Observación de ejemplo para la sesión. El paciente mostró una respuesta positiva durante la terapia.' as content,
    NOW() as created_at,
    NOW() as updated_at,
    1 as version,
    FALSE as is_deleted
FROM sessions s 
LIMIT 5;

-- Actualizar algunas sesiones con niveles de exposición de ejemplo
UPDATE sessions SET exposure_level = 'ALTO', duration = 45, status = 'Completada' WHERE id_session IN (
    SELECT id_session FROM (SELECT id_session FROM sessions LIMIT 2) as temp
);

UPDATE sessions SET exposure_level = 'MEDIO', duration = 30, status = 'Completada' WHERE id_session IN (
    SELECT id_session FROM (SELECT id_session FROM sessions LIMIT 2 OFFSET 2) as temp
);

UPDATE sessions SET exposure_level = 'BAJO', duration = 20, status = 'En Progreso' WHERE id_session IN (
    SELECT id_session FROM (SELECT id_session FROM sessions LIMIT 1 OFFSET 4) as temp
);