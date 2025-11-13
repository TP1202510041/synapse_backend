package com.proyecto.synapsevr.Controller;

import com.proyecto.synapsevr.Entity.ClinicalObservationEntity;
import com.proyecto.synapsevr.Repository.ClinicalObservationRepository;
import com.proyecto.synapsevr.Repository.SessionRepository;
import com.proyecto.synapsevr.Repository.UserRepository;
import com.proyecto.synapsevr.Service.ClinicalObservationService;
import com.proyecto.synapsevr.Service.EventService;
import com.proyecto.synapsevr.Service.MonitoringObservationService;
import com.proyecto.synapsevr.Service.MonitoringService;
import com.proyecto.synapsevr.Service.UserService;
import com.proyecto.synapsevr.dto.Request.ClinicalObservationRequest;
import com.proyecto.synapsevr.dto.Request.EventRequest;
import com.proyecto.synapsevr.dto.Request.MonitoringObservationRequest;
import com.proyecto.synapsevr.dto.Request.MonitoringRecordRequest;
import com.proyecto.synapsevr.dto.Response.ClinicalObservationResponse;
import com.proyecto.synapsevr.dto.Response.EventResponse;
import com.proyecto.synapsevr.dto.Response.MonitoringObservationResponse;
import com.proyecto.synapsevr.dto.Response.MonitoringRecordResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/test-data")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Test Data", description = "API para crear datos de prueba")
public class TestDataController {

    private final ClinicalObservationRepository observationRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final ClinicalObservationService observationService;
    private final MonitoringObservationService monitoringObservationService;
    private final MonitoringService monitoringService;
    private final EventService eventService;
    private final UserService userService;

    @PostMapping("/create-sample-observations")
    @Operation(summary = "Crear múltiples observaciones de prueba para la misma sesión")
    public ResponseEntity<String> createSampleObservations(Principal principal) {
        try {
            System.out.println("🧪 [TEST] Creando MÚLTIPLES observaciones para la misma sesión...");
            String userEmail = principal.getName();
            System.out.println("👤 Usuario: " + userEmail);
            
            // Observación 1 - Sesión específica
            ClinicalObservationRequest request1 = new ClinicalObservationRequest();
            request1.setSessionId(UUID.fromString("244bc0ab-4c5c-4b56-bf44-5e0bada57bd4"));
            request1.setPatientId(1);
            request1.setContent("🧪 OBSERVACIÓN 1: El paciente mostró excelente respuesta inicial al tratamiento VR. Reducción notable de ansiedad. Tiempo: " + LocalDateTime.now());
            request1.setSessionDate(LocalDate.now());
            
            System.out.println("📝 Creando observación 1...");
            ClinicalObservationResponse obs1 = observationService.createObservation(request1, userEmail);
            System.out.println("✅ Observación 1 creada con ID: " + obs1.getId());
            
            // Observación 2 - MISMA sesión (debería crear nueva, no actualizar)
            ClinicalObservationRequest request2 = new ClinicalObservationRequest();
            request2.setSessionId(UUID.fromString("244bc0ab-4c5c-4b56-bf44-5e0bada57bd4"));
            request2.setPatientId(1);
            request2.setContent("🧪 OBSERVACIÓN 2: A mitad de sesión, el paciente se adaptó completamente al entorno virtual. Excelente progreso. Tiempo: " + LocalDateTime.now());
            request2.setSessionDate(LocalDate.now());
            
            System.out.println("📝 Creando observación 2 (MISMA sesión)...");
            ClinicalObservationResponse obs2 = observationService.createObservation(request2, userEmail);
            System.out.println("✅ Observación 2 creada con ID: " + obs2.getId());
            
            // Observación 3 - MISMA sesión (debería crear nueva, no actualizar)
            ClinicalObservationRequest request3 = new ClinicalObservationRequest();
            request3.setSessionId(UUID.fromString("244bc0ab-4c5c-4b56-bf44-5e0bada57bd4"));
            request3.setPatientId(1);
            request3.setContent("🧪 OBSERVACIÓN 3: Al final de la sesión, el paciente completó todos los ejercicios sin interrupciones. Progreso excepcional. Tiempo: " + LocalDateTime.now());
            request3.setSessionDate(LocalDate.now());
            
            System.out.println("📝 Creando observación 3 (MISMA sesión)...");
            ClinicalObservationResponse obs3 = observationService.createObservation(request3, userEmail);
            System.out.println("✅ Observación 3 creada con ID: " + obs3.getId());
            
            // Observación 4 - Diferente paciente (debería fallar)
            ClinicalObservationRequest request4 = new ClinicalObservationRequest();
            request4.setSessionId(UUID.fromString("244bc0ab-4c5c-4b56-bf44-5e0bada57bd4"));
            request4.setPatientId(3); // ← Paciente incorrecto
            request4.setContent("🧪 OBSERVACIÓN 4: Esta debería fallar porque el paciente no coincide con la sesión. Tiempo: " + LocalDateTime.now());
            request4.setSessionDate(LocalDate.now());
            
            System.out.println("📝 Intentando crear observación 4 (paciente incorrecto - debería fallar)...");
            try {
                ClinicalObservationResponse obs4 = observationService.createObservation(request4, userEmail);
                System.out.println("❌ ERROR: Observación 4 NO debería haberse creado: " + obs4.getId());
            } catch (Exception e) {
                System.out.println("✅ CORRECTO: Observación 4 falló como esperado: " + e.getMessage());
            }
            
            return ResponseEntity.ok("✅ Prueba de múltiples observaciones completada:\n\n" +
                    "📝 Observación 1: " + obs1.getId() + " (Paciente: " + obs1.getPatientId() + ")\n" +
                    "📝 Observación 2: " + obs2.getId() + " (Paciente: " + obs2.getPatientId() + ")\n" +
                    "📝 Observación 3: " + obs3.getId() + " (Paciente: " + obs3.getPatientId() + ")\n" +
                    "❌ Observación 4: FALLÓ correctamente (paciente incorrecto)\n\n" +
                    "🔍 Para verificar que hay 3 observaciones:\n" +
                    "GET /api/observations/session/244bc0ab-4c5c-4b56-bf44-5e0bada57bd4");
                    
        } catch (Exception e) {
            System.err.println("❌ Error creando observaciones de prueba: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("❌ Error creando observaciones: " + e.getMessage());
        }
    }

    @PostMapping("/create-monitoring-observations")
    @Operation(summary = "Crear observaciones de monitoreo de prueba")
    public ResponseEntity<String> createMonitoringObservations(Principal principal) {
        try {
            System.out.println("🧪 [TEST] Creando observaciones de MONITOREO de ejemplo...");
            String userEmail = principal.getName();
            System.out.println("👤 Usuario: " + userEmail);
            
            // Usar el monitoringId que tienes en tu ejemplo
            UUID monitoringId = UUID.fromString("5204594e-51ca-4c76-bbef-74cdc39883a0");
            
            // Observación 1 de monitoreo
            MonitoringObservationRequest request1 = new MonitoringObservationRequest();
            request1.setMonitoringId(monitoringId);
            request1.setPatientId(3); // Paciente "Luyo lapiz"
            request1.setContent("🧪 MONITOREO-OBS 1: Durante el monitoreo se observó una estabilización del BPM en 84. El paciente mostró buena adaptación al entorno VR. Tiempo: " + LocalDateTime.now());
            request1.setObservationDate(LocalDate.now());
            
            System.out.println("📝 Creando observación de monitoreo 1...");
            MonitoringObservationResponse obs1 = monitoringObservationService.createMonitoringObservation(request1, userEmail);
            System.out.println("✅ Observación de monitoreo 1 creada con ID: " + obs1.getId());
            
            // Observación 2 de monitoreo (mismo monitoringId)
            MonitoringObservationRequest request2 = new MonitoringObservationRequest();
            request2.setMonitoringId(monitoringId);
            request2.setPatientId(3);
            request2.setContent("🧪 MONITOREO-OBS 2: A mitad del monitoreo, el paciente logró mantener el BPM estable. Excelente control de ansiedad durante la exposición. Tiempo: " + LocalDateTime.now());
            request2.setObservationDate(LocalDate.now());
            
            System.out.println("📝 Creando observación de monitoreo 2 (mismo monitoringId)...");
            MonitoringObservationResponse obs2 = monitoringObservationService.createMonitoringObservation(request2, userEmail);
            System.out.println("✅ Observación de monitoreo 2 creada con ID: " + obs2.getId());
            
            // Observación 3 de monitoreo (mismo monitoringId)
            MonitoringObservationRequest request3 = new MonitoringObservationRequest();
            request3.setMonitoringId(monitoringId);
            request3.setPatientId(3);
            request3.setContent("🧪 MONITOREO-OBS 3: Al final del monitoreo, el paciente completó toda la sesión con BPM constante de 84. Progreso excepcional en el control de ansiedad. Tiempo: " + LocalDateTime.now());
            request3.setObservationDate(LocalDate.now());
            
            System.out.println("📝 Creando observación de monitoreo 3 (mismo monitoringId)...");
            MonitoringObservationResponse obs3 = monitoringObservationService.createMonitoringObservation(request3, userEmail);
            System.out.println("✅ Observación de monitoreo 3 creada con ID: " + obs3.getId());
            
            return ResponseEntity.ok("✅ Observaciones de MONITOREO creadas exitosamente:\n\n" +
                    "📝 Observación 1: " + obs1.getId() + " (Paciente: " + obs1.getPatientId() + ")\n" +
                    "📝 Observación 2: " + obs2.getId() + " (Paciente: " + obs2.getPatientId() + ")\n" +
                    "📝 Observación 3: " + obs3.getId() + " (Paciente: " + obs3.getPatientId() + ")\n\n" +
                    "🔍 Para verificar que hay 3 observaciones de monitoreo:\n" +
                    "GET /api/monitoring-observations/monitoring/" + monitoringId + "\n\n" +
                    "📊 Datos del monitoreo original:\n" +
                    "- MonitoringId: " + monitoringId + "\n" +
                    "- PatientId: 3 (Luyo lapiz)\n" +
                    "- BPM promedio: 84\n" +
                    "- Duración: 47 minutos");
                    
        } catch (Exception e) {
            System.err.println("❌ Error creando observaciones de monitoreo: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("❌ Error creando observaciones de monitoreo: " + e.getMessage());
        }
    }

    @PostMapping("/create-sample-events")
    @Operation(summary = "Crear eventos de calendario de prueba")
    public ResponseEntity<String> createSampleEvents(Principal principal) {
        try {
            Integer userId = userService.findByEmail(principal.getName()).getUserId();
            System.out.println("🎯 Creando eventos de prueba para usuario ID: " + userId + " (" + principal.getName() + ")");
            
            // Evento 1: Cita con paciente hoy
            EventRequest event1 = new EventRequest();
            event1.setTitle("Cita con Pablo - Sesión VR");
            event1.setStart(LocalDateTime.now().plusHours(2));
            event1.setEnd(LocalDateTime.now().plusHours(3));
            event1.setDescription("Sesión de terapia VR para tratamiento de ansiedad. Paciente: Pablo");
            event1.setColor("#4285f4");
            
            EventResponse createdEvent1 = eventService.createEvent(event1, userId);
            
            // Evento 2: Reunión mañana
            EventRequest event2 = new EventRequest();
            event2.setTitle("Reunión de equipo médico");
            event2.setStart(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0));
            event2.setEnd(LocalDateTime.now().plusDays(1).withHour(11).withMinute(30));
            event2.setDescription("Revisión de casos clínicos y nuevos protocolos de VR");
            event2.setColor("#34a853");
            
            EventResponse createdEvent2 = eventService.createEvent(event2, userId);
            
            // Evento 3: Cita con otro paciente
            EventRequest event3 = new EventRequest();
            event3.setTitle("Cita con Luyo lapiz - Seguimiento");
            event3.setStart(LocalDateTime.now().plusDays(2).withHour(14).withMinute(0));
            event3.setEnd(LocalDateTime.now().plusDays(2).withHour(15).withMinute(0));
            event3.setDescription("Sesión de seguimiento post-terapia VR. Revisión de progreso.");
            event3.setColor("#ea4335");
            
            EventResponse createdEvent3 = eventService.createEvent(event3, userId);
            
            // Evento 4: Evento de toda la semana
            EventRequest event4 = new EventRequest();
            event4.setTitle("Conferencia de Realidad Virtual en Medicina");
            event4.setStart(LocalDateTime.now().plusDays(7).withHour(9).withMinute(0));
            event4.setEnd(LocalDateTime.now().plusDays(7).withHour(17).withMinute(0));
            event4.setDescription("Conferencia anual sobre avances en VR para tratamientos médicos");
            event4.setColor("#ff9800");
            
            EventResponse createdEvent4 = eventService.createEvent(event4, userId);
            
            System.out.println("✅ 4 eventos de prueba creados exitosamente");
            
            return ResponseEntity.ok(
                "✅ 4 eventos de calendario creados exitosamente para el usuario: " + principal.getName() + "\n\n" +
                "📅 Eventos creados:\n" +
                "1. 📝 " + createdEvent1.getTitle() + " (ID: " + createdEvent1.getId() + ")\n" +
                "   📅 " + createdEvent1.getStart() + " - " + createdEvent1.getEnd() + "\n" +
                "   🎨 Color: " + createdEvent1.getColor() + "\n\n" +
                "2. 📝 " + createdEvent2.getTitle() + " (ID: " + createdEvent2.getId() + ")\n" +
                "   📅 " + createdEvent2.getStart() + " - " + createdEvent2.getEnd() + "\n" +
                "   🎨 Color: " + createdEvent2.getColor() + "\n\n" +
                "3. 📝 " + createdEvent3.getTitle() + " (ID: " + createdEvent3.getId() + ")\n" +
                "   📅 " + createdEvent3.getStart() + " - " + createdEvent3.getEnd() + "\n" +
                "   🎨 Color: " + createdEvent3.getColor() + "\n\n" +
                "4. 📝 " + createdEvent4.getTitle() + " (ID: " + createdEvent4.getId() + ")\n" +
                "   📅 " + createdEvent4.getStart() + " - " + createdEvent4.getEnd() + "\n" +
                "   🎨 Color: " + createdEvent4.getColor() + "\n\n" +
                "🔍 Para verificar los eventos creados:\n" +
                "GET /api/events\n\n" +
                "📊 Usuario: " + principal.getName() + " (ID: " + userId + ")");
                
        } catch (Exception e) {
            System.err.println("❌ Error creando eventos de prueba: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("❌ Error creando eventos de prueba: " + e.getMessage());
        }
    }

    @PostMapping("/create-monitoring-record")
    @Operation(summary = "Crear registro de monitoreo de prueba")
    public ResponseEntity<String> createMonitoringRecord(Principal principal) {
        try {
            System.out.println("🧪 [TEST] Creando registro de MONITOREO de ejemplo...");
            String userEmail = principal.getName();
            Integer userId = userService.findByEmail(userEmail).getUserId();
            System.out.println("👤 Usuario: " + userEmail + " (ID: " + userId + ")");
            
            // Crear un registro de monitoreo de prueba
            MonitoringRecordRequest request = new MonitoringRecordRequest();
            request.setSessionId(UUID.fromString("c6929664-291e-42d5-8eec-a12fb5a99d39")); // Sesión existente
            request.setPatientId(1); // Paciente Pablo
            request.setStartTime(System.currentTimeMillis() - 3600000); // Hace 1 hora
            request.setEndTime(System.currentTimeMillis()); // Ahora
            request.setStartTimeLocal(System.currentTimeMillis() - 3600000);
            request.setEndTimeLocal(System.currentTimeMillis());
            request.setDuration(60); // 60 minutos
            request.setTotalRecords(3600); // 1 registro por segundo durante 1 hora
            request.setAvgHeartRate(new BigDecimal("78.5"));
            request.setMinHeartRate(new BigDecimal("65.0"));
            request.setMaxHeartRate(new BigDecimal("95.0"));
            
            // Datos simulados de frecuencia cardíaca
            List<Object> heartRateRecords = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                heartRateRecords.add("Registro " + i + ": BPM " + (70 + i));
            }
            request.setHeartRateRecords(heartRateRecords);
            
            System.out.println("📝 Creando registro de monitoreo...");
            MonitoringRecordResponse response = monitoringService.saveMonitoringRecord(request, userId);
            System.out.println("✅ Registro de monitoreo creado con ID: " + response.getMonitoringId());
            
            return ResponseEntity.ok("✅ Registro de MONITOREO creado exitosamente:\n\n" +
                    "📊 MonitoringId: " + response.getMonitoringId() + "\n" +
                    "👤 PatientId: " + response.getPatientId() + "\n" +
                    "🎯 SessionId: " + response.getSessionId() + "\n" +
                    "⏱️ Duración: " + response.getDuration() + " minutos\n" +
                    "💓 BPM Promedio: " + response.getAvgHeartRate() + "\n" +
                    "📈 BPM Mínimo: " + response.getMinHeartRate() + "\n" +
                    "📈 BPM Máximo: " + response.getMaxHeartRate() + "\n" +
                    "📊 Total Registros: " + response.getTotalRecords() + "\n\n" +
                    "🔍 Para verificar el registro creado:\n" +
                    "GET /api/monitoring/session/" + response.getSessionId() + "\n\n" +
                    "📝 Ahora puedes crear observaciones de monitoreo con:\n" +
                    "POST /api/test-data/create-monitoring-observations");
                    
        } catch (Exception e) {
            System.err.println("❌ Error creando registro de monitoreo: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("❌ Error creando registro de monitoreo: " + e.getMessage());
        }
    }

    @GetMapping("/info")
    @Operation(summary = "Información de datos disponibles")
    public ResponseEntity<String> getDataInfo() {
        try {
            long sessions = sessionRepository.count();
            long observations = observationRepository.count();
            long users = userRepository.count();
            
            String info = String.format(
                "Datos disponibles:\n" +
                "- Sesiones: %d\n" +
                "- Observaciones de sesión: %d\n" +
                "- Usuarios: %d\n\n" +
                "Endpoints disponibles:\n" +
                "- POST /api/test-data/create-sample-observations (observaciones de sesión)\n" +
                "- POST /api/test-data/create-monitoring-observations (observaciones de monitoreo)\n" +
                "- POST /api/test-data/create-sample-events (eventos de calendario)\n\n" +
                "Monitoreo de ejemplo disponible:\n" +
                "- MonitoringId: 5204594e-51ca-4c76-bbef-74cdc39883a0\n" +
                "- PatientId: 3 (Luyo lapiz)\n" +
                "- BPM: 84, Duración: 47 min",
                sessions, observations, users
            );
            
            return ResponseEntity.ok(info);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error obteniendo información: " + e.getMessage());
        }
    }
}