-- Migración V4: Crear tabla de eventos del calendario
-- Fecha: 2025-10-04
-- Descripción: Tabla para almacenar eventos del calendario de terapeutas

CREATE TABLE IF NOT EXISTS events (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    description TEXT,
    color VARCHAR(7) NOT NULL DEFAULT '#4285f4',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Índices para mejorar rendimiento
    INDEX idx_events_user_id (user_id),
    INDEX idx_events_start_time (start_time),
    INDEX idx_events_end_time (end_time),
    INDEX idx_events_date_range (user_id, start_time, end_time),
    
    -- Clave foránea
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Comentarios para documentación
ALTER TABLE events COMMENT = 'Tabla para almacenar eventos del calendario de terapeutas';
ALTER TABLE events MODIFY COLUMN id INT AUTO_INCREMENT COMMENT 'ID único del evento';
ALTER TABLE events MODIFY COLUMN user_id INT NOT NULL COMMENT 'ID del terapeuta propietario del evento';
ALTER TABLE events MODIFY COLUMN title VARCHAR(255) NOT NULL COMMENT 'Título del evento';
ALTER TABLE events MODIFY COLUMN start_time TIMESTAMP NOT NULL COMMENT 'Fecha y hora de inicio del evento';
ALTER TABLE events MODIFY COLUMN end_time TIMESTAMP NOT NULL COMMENT 'Fecha y hora de fin del evento';
ALTER TABLE events MODIFY COLUMN description TEXT COMMENT 'Descripción detallada del evento';
ALTER TABLE events MODIFY COLUMN color VARCHAR(7) NOT NULL DEFAULT '#4285f4' COMMENT 'Color del evento en formato hexadecimal';
ALTER TABLE events MODIFY COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha de creación del registro';
ALTER TABLE events MODIFY COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Fecha de última actualización';