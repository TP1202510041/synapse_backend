-- Agregar nuevos campos a la tabla users
ALTER TABLE users
ADD COLUMN phone_number VARCHAR(20),
ADD COLUMN dni VARCHAR(20) UNIQUE,
ADD COLUMN address VARCHAR(500),
ADD COLUMN birth_date DATE,
ADD COLUMN gender VARCHAR(10),
ADD COLUMN professional_license VARCHAR(50),
ADD COLUMN specialization VARCHAR(100),
ADD COLUMN account_status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
ADD COLUMN email_verified BOOLEAN NOT NULL DEFAULT FALSE,
ADD COLUMN failed_login_attempts INT NOT NULL DEFAULT 0,
ADD COLUMN account_locked_until DATETIME,
ADD COLUMN last_login DATETIME,
ADD COLUMN password_changed_at DATETIME;

-- Crear tabla para intentos de login
CREATE TABLE IF NOT EXISTS login_attempts (
    attempt_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    successful BOOLEAN NOT NULL DEFAULT FALSE,
    attempt_time DATETIME NOT NULL,
    failure_reason VARCHAR(255),
    INDEX idx_email (email),
    INDEX idx_attempt_time (attempt_time),
    INDEX idx_email_time (email, attempt_time)
);

-- Actualizar roles existentes si es necesario
-- ALTER TABLE users MODIFY COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER';

-- Comentarios para documentación
ALTER TABLE users 
MODIFY COLUMN phone_number VARCHAR(20) COMMENT 'Número de teléfono del usuario',
MODIFY COLUMN dni VARCHAR(20) COMMENT 'Documento Nacional de Identidad',
MODIFY COLUMN address VARCHAR(500) COMMENT 'Dirección del usuario',
MODIFY COLUMN birth_date DATE COMMENT 'Fecha de nacimiento',
MODIFY COLUMN gender VARCHAR(10) COMMENT 'Género: MASCULINO, FEMENINO, OTRO, PREFIERO_NO_DECIR',
MODIFY COLUMN professional_license VARCHAR(50) COMMENT 'Licencia profesional para terapeutas',
MODIFY COLUMN specialization VARCHAR(100) COMMENT 'Especialización profesional',
MODIFY COLUMN account_status VARCHAR(30) COMMENT 'Estado: ACTIVE, LOCKED, SUSPENDED, PENDING_VERIFICATION, DISABLED',
MODIFY COLUMN email_verified BOOLEAN COMMENT 'Si el email ha sido verificado',
MODIFY COLUMN failed_login_attempts INT COMMENT 'Contador de intentos fallidos de login',
MODIFY COLUMN account_locked_until DATETIME COMMENT 'Fecha hasta la cual la cuenta está bloqueada',
MODIFY COLUMN last_login DATETIME COMMENT 'Último login exitoso',
MODIFY COLUMN password_changed_at DATETIME COMMENT 'Última vez que se cambió la contraseña';
