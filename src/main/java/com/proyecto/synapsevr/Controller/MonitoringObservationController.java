package com.proyecto.synapsevr.Controller;

import com.proyecto.synapsevr.Service.MonitoringObservationService;
import com.proyecto.synapsevr.dto.Request.MonitoringObservationRequest;
import com.proyecto.synapsevr.dto.Response.ApiResponse;
import com.proyecto.synapsevr.dto.Response.MonitoringObservationResponse;
import com.proyecto.synapsevr.dto.Response.PaginatedMonitoringObservationsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/monitoring-observations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Monitoring Observations", description = "API para observaciones clínicas de monitoreo")
public class MonitoringObservationController {

    private final MonitoringObservationService observationService;

    @PostMapping
    @Operation(summary = "Crear nueva observación de monitoreo")
    public ResponseEntity<ApiResponse<MonitoringObservationResponse>> createMonitoringObservation(
            @Valid @RequestBody MonitoringObservationRequest request,
            Principal principal) {
        try {
            MonitoringObservationResponse observation = observationService.createMonitoringObservation(request, principal.getName());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Observación de monitoreo creada exitosamente", observation));
        } catch (RuntimeException e) {
            System.err.println("❌ Error creando observación de monitoreo: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            System.err.println("❌ Error interno: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor"));
        }
    }

    @GetMapping("/monitoring/{monitoringId}")
    @Operation(summary = "Obtener observaciones por ID de monitoreo")
    public ResponseEntity<ApiResponse<List<MonitoringObservationResponse>>> getObservationsByMonitoringId(
            @PathVariable UUID monitoringId) {
        try {
            List<MonitoringObservationResponse> observations = observationService.getObservationsByMonitoringId(monitoringId);
            return ResponseEntity.ok(ApiResponse.success("Observaciones obtenidas exitosamente", observations));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor"));
        }
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Obtener observaciones de monitoreo por paciente (paginado)")
    public ResponseEntity<ApiResponse<PaginatedMonitoringObservationsResponse>> getObservationsByPatient(
            @PathVariable Integer patientId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        try {
            PaginatedMonitoringObservationsResponse observations = observationService.getObservationsByPatient(
                    patientId, page, limit, sortBy, sortOrder);
            return ResponseEntity.ok(ApiResponse.success("Observaciones obtenidas exitosamente", observations));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor"));
        }
    }

    @GetMapping("/therapist")
    @Operation(summary = "Obtener observaciones de monitoreo del terapeuta actual")
    public ResponseEntity<ApiResponse<List<MonitoringObservationResponse>>> getObservationsByTherapist(
            Principal principal) {
        try {
            List<MonitoringObservationResponse> observations = observationService.getObservationsByTherapist(principal.getName());
            return ResponseEntity.ok(ApiResponse.success("Observaciones obtenidas exitosamente", observations));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor"));
        }
    }

    @PutMapping("/{observationId}")
    @Operation(summary = "Actualizar observación de monitoreo")
    public ResponseEntity<ApiResponse<MonitoringObservationResponse>> updateMonitoringObservation(
            @PathVariable UUID observationId,
            @Valid @RequestBody MonitoringObservationRequest request,
            Principal principal) {
        try {
            MonitoringObservationResponse observation = observationService.updateMonitoringObservation(
                    observationId, request, principal.getName());
            return ResponseEntity.ok(ApiResponse.success("Observación actualizada exitosamente", observation));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor"));
        }
    }

    @DeleteMapping("/{observationId}")
    @Operation(summary = "Eliminar observación de monitoreo")
    public ResponseEntity<ApiResponse<Void>> deleteMonitoringObservation(
            @PathVariable UUID observationId,
            Principal principal) {
        try {
            observationService.deleteMonitoringObservation(observationId, principal.getName());
            return ResponseEntity.ok(ApiResponse.success("Observación eliminada exitosamente", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor"));
        }
    }
}