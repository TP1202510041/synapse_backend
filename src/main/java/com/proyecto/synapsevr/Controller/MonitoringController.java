package com.proyecto.synapsevr.Controller;

import com.proyecto.synapsevr.Entity.UserEntity;
import com.proyecto.synapsevr.Service.MonitoringService;
import com.proyecto.synapsevr.Repository.UserRepository;
import com.proyecto.synapsevr.Entity.UserEntity;
import com.proyecto.synapsevr.dto.Request.MonitoringRecordRequest;
import com.proyecto.synapsevr.dto.Response.MonitoringRecordResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/monitoring")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
@Slf4j
public class MonitoringController {

    private final MonitoringService monitoringService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> saveMonitoringRecord(@RequestBody MonitoringRecordRequest request,
                                                  Principal principal) {
        try {
            log.info("📊 Guardando registro de monitoreo para usuario: {}", principal.getName());

            // Obtener email del Principal
            String userEmail = principal.getName();

            // Buscar usuario por email
            UserEntity user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Guardar registro con el userId correcto
            MonitoringRecordResponse response = monitoringService.saveMonitoringRecord(request, user.getUserId());

            log.info("✅ Registro guardado exitosamente para usuario ID: {}", user.getUserId());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error guardando registro de monitoreo: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving monitoring record: " + e.getMessage());
        }
    }

    @GetMapping("/user")
    public ResponseEntity<List<MonitoringRecordResponse>> getMonitoringRecordsByUser(Principal principal) {
        try {
            // Obtener email del Principal
            String userEmail = principal.getName();

            // Buscar usuario por email
            UserEntity user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            List<MonitoringRecordResponse> records = monitoringService.getMonitoringRecordsByUser(user.getUserId());
            return ResponseEntity.ok(records);

        } catch (Exception e) {
            log.error("Error fetching user monitoring records: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<MonitoringRecordResponse>> getMonitoringRecordsBySession(
            @PathVariable UUID sessionId,
            Principal principal) {
        try {
            log.info("📊 Obteniendo registros de monitoreo para sesión: {}", sessionId);

            // Obtener email del Principal
            String userEmail = principal.getName();

            // Buscar usuario por email
            UserEntity user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Obtener registros de la sesión
            List<MonitoringRecordResponse> records = monitoringService
                    .getMonitoringRecordsBySession(sessionId, user.getUserId());

            log.info("✅ Se encontraron {} registros para la sesión {}", records.size(), sessionId);
            return ResponseEntity.ok(records);

        } catch (Exception e) {
            log.error("❌ Error obteniendo registros de sesión {}: ", sessionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMonitoringRecord(@PathVariable UUID id, Principal principal) {
        try {
            // Obtener email del Principal
            String userEmail = principal.getName();

            // Buscar usuario por email
            UserEntity user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            monitoringService.deleteMonitoringRecord(id, user.getUserId());
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            log.error("Error deleting monitoring record: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting monitoring record");
        }
    }

}