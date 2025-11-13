package com.proyecto.synapsevr.Controller;

import com.proyecto.synapsevr.dto.Request.SessionRequest;
import com.proyecto.synapsevr.dto.Request.UpdateSessionRequest;
import com.proyecto.synapsevr.dto.Response.SessionResponse;
import com.proyecto.synapsevr.dto.Response.CalendarSessionResponse;
import com.proyecto.synapsevr.dto.Response.FilteredSessionsResponse;
import com.proyecto.synapsevr.Service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/sessions")
@CrossOrigin(origins = "http://localhost:4200")
public class SessionController {
    
    @Autowired
    private SessionService sessionService;
    
    // 📋 GET: Obtener todas las sesiones de un paciente
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<SessionResponse>> getSessionsByPatient(@PathVariable int patientId) {
        try {
            List<SessionResponse> sessions = sessionService.getSessionsByPatientId(patientId);
            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // 📅 GET: Obtener sesión de HOY de un paciente
    @GetMapping("/patient/{patientId}/today")
    public ResponseEntity<SessionResponse> getTodaySession(@PathVariable int patientId) {
        try {
            Optional<SessionResponse> session = sessionService.getTodaySessionByPatientId(patientId);
            return session.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // 🕐 GET: Obtener ÚLTIMA sesión de un paciente
    @GetMapping("/patient/{patientId}/latest")
    public ResponseEntity<SessionResponse> getLatestSession(@PathVariable int patientId) {
        try {
            Optional<SessionResponse> session = sessionService.getLatestSessionByPatientId(patientId);
            return session.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ➕ POST: Crear nueva sesión
    @PostMapping
    public ResponseEntity<SessionResponse> createSession(
            @RequestBody SessionRequest createSession,
            Principal principal) {
        try {
            String userEmail = principal.getName(); // Email del usuario logueado
            SessionResponse createdSession = sessionService.createSession(createSession, userEmail);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSession);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // 👁️ GET: Obtener sesión por ID (para botón "Ver")
    @GetMapping("/{sessionId}")
    public ResponseEntity<SessionResponse> getSessionById(@PathVariable UUID sessionId) {
        try {
            Optional<SessionResponse> session = sessionService.getSessionById(sessionId);
            return session.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ✏️ PUT: Actualizar sesión existente
    @PutMapping("/{sessionId}")
    public ResponseEntity<SessionResponse> updateSession(
            @PathVariable UUID sessionId,
            @RequestBody UpdateSessionRequest updateRequest,
            Principal principal) {
        try {
            String userEmail = principal.getName();
            SessionResponse updatedSession = sessionService.updateSession(sessionId, updateRequest, userEmail);
            return ResponseEntity.ok(updatedSession);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // 🗑️ DELETE: Eliminar sesión
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteSession(@PathVariable UUID sessionId, Principal principal) {
        try {
            String userEmail = principal.getName();
            boolean deleted = sessionService.deleteSession(sessionId, userEmail);
            return deleted ? ResponseEntity.noContent().build() 
                          : ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 📆 GET: Obtener sesiones para el calendario (con parámetros opcionales)
    @GetMapping("/calendar")
    public ResponseEntity<List<CalendarSessionResponse>> getSessionsForCalendar(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        try {
            // Si no se envían parámetros, usar valores por defecto del mes actual
            if (startDate == null) {
                startDate = LocalDate.now().withDayOfMonth(1); // Primer día del mes actual
            }
            if (endDate == null) {
                endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()); // Último día del mes actual
            }

            List<CalendarSessionResponse> sessions = sessionService.getSessionsForCalendar(startDate, endDate);
            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 🔍 GET: Obtener sesiones con filtros (HU-006-006)
    @GetMapping("/patient/{patientId}/filtered")
    public ResponseEntity<FilteredSessionsResponse> getFilteredSessions(
            @PathVariable Integer patientId,
            @RequestParam(required = false) List<String> exposureLevel,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "sessionDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        try {
            FilteredSessionsResponse sessions = sessionService.getSessionsWithFilters(
                    patientId, exposureLevel, dateFrom, dateTo, page, limit, sortBy, sortOrder);
            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}