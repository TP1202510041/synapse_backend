# SynapseVR API

API REST para el proyecto SynapseVR desarrollada con Spring Boot.

## 🚀 Configuración del Proyecto

### Prerrequisitos

- Java 17 o superior
- Maven 3.6+
- MySQL 8.0+
- IDE (IntelliJ IDEA, Eclipse, VS Code)

### Configuración de Base de Datos

1. **Instalar MySQL** y asegurarse de que esté ejecutándose en el puerto 3306

2. **Crear la base de datos:**
   ```sql
   CREATE DATABASE synapsevr_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

3. **Configurar credenciales** en `src/main/resources/application.properties`:
   ```properties
   spring.datasource.username=tu_usuario
   spring.datasource.password=tu_contraseña
   ```

### Ejecutar la Aplicación

1. **Clonar el repositorio**
   ```bash
   git clone <url-del-repositorio>
   cd synapsevr
   ```

2. **Instalar dependencias**
   ```bash
   mvn clean install
   ```

3. **Ejecutar la aplicación**
   ```bash
   mvn spring-boot:run
   ```

La aplicación estará disponible en: `http://localhost:8080`

## 📚 Documentación de la API

### Swagger UI
Una vez que la aplicación esté ejecutándose, puedes acceder a la documentación interactiva de la API:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

### Endpoints Principales

#### Autenticación
- `POST /api/auth/register` - Registrar nuevo usuario
- `POST /api/auth/login` - Iniciar sesión
- `GET /api/auth/check-email` - Verificar si email existe
- `GET /api/auth/test` - Endpoint de prueba

#### Pacientes
- `GET /api/patients` - Obtener todos los pacientes
- `GET /api/patients/{id}` - Obtener paciente por ID
- `POST /api/patients` - Crear nuevo paciente
- `PUT /api/patients/{id}` - Actualizar paciente
- `DELETE /api/patients/{id}` - Eliminar paciente

## 🔐 Autenticación

La API utiliza JWT (JSON Web Tokens) para la autenticación. Para acceder a endpoints protegidos:

1. Registrarse o iniciar sesión para obtener un token
2. Incluir el token en el header de las peticiones:
   ```
   Authorization: Bearer <tu-jwt-token>
   ```

## 🛠️ Tecnologías Utilizadas

- **Spring Boot 3.5.5**
- **Spring Security** - Autenticación y autorización
- **Spring Data JPA** - Persistencia de datos
- **MySQL** - Base de datos principal
- **MongoDB** - Base de datos secundaria (opcional)
- **JWT** - Autenticación basada en tokens
- **Swagger/OpenAPI 3** - Documentación de API
- **Lombok** - Reducción de código boilerplate
- **Maven** - Gestión de dependencias

## 📁 Estructura del Proyecto

```
src/main/java/com/proyecto/synapsevr/
├── Config/          # Configuraciones (Swagger, Security, etc.)
├── Controller/      # Controladores REST
├── dto/            # Data Transfer Objects
├── Entity/         # Entidades JPA
├── Repository/     # Repositorios de datos
├── Security/       # Configuración de seguridad
├── Service/        # Lógica de negocio
└── SynapsevrApplication.java
```

## 🔧 Configuración Adicional

### Variables de Entorno
Puedes configurar las siguientes variables de entorno:

```bash
DB_HOST=localhost
DB_PORT=3306
DB_NAME=synapsevr_db
DB_USERNAME=root
DB_PASSWORD=password
JWT_SECRET=mySecretKey123456789012345678901234567890
```

### Perfiles de Spring
- `dev` - Desarrollo (por defecto)
- `prod` - Producción
- `test` - Pruebas

## 🧪 Pruebas

Ejecutar las pruebas:
```bash
mvn test
```

## 📝 Notas de Desarrollo

- Las tablas de la base de datos se crean automáticamente con `spring.jpa.hibernate.ddl-auto=update`
- Los logs están configurados para mostrar las consultas SQL en modo DEBUG
- CORS está habilitado para desarrollo local
- La aplicación incluye validación de datos y manejo de errores

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request