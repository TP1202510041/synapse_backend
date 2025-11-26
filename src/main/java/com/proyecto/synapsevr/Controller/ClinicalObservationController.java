package com.proyecto.synapsevr.Controller;

import com.proyecto.synapsevr.dto.Request.ClinicalObservationRequest;
import com.proyecto.synapsevr.dto.Request.UpdateObservationRequest;
import com.proyecto.synapsevr.dto.Response.ApiResponse;
import com.proyecto.synapsevr.dto.Response.ClinicalObservationResponse;
import com.proyecto.synapsevr.dto.Response.PaginatedObservationsResponse;
import com.proyecto.synapsevr.Service.ClinicalObservationService;
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
@RequestMapping("/api/observations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Clinical Observations", description = "API para gestión de observaciones clínicas")
public class ClinicalObservationController {

    private final ClinicalObservationService observationService;

    @PostMapping
    @Operation(summary = "Crear nueva observación clínica")
    public ResponseEntity<ApiResponse<ClinicalObservationResponse>> createObservation(
            @Valid @RequestBody ClinicalObservationRequest request,
            Principal principal) {
        try {
            ClinicalObservationResponse observation = observationService.createObservation(request, principal.getName());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Observación creada exitosamente", observation));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor"));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar observación existente")
    public ResponseEntity<ApiResponse<ClinicalObservationResponse>> updateObservation(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateObservationRequest request,
            Principal principal) {
        try {
            ClinicalObservationResponse observation = observationService.updateObservation(id, request, principal.getName());
            return ResponseEntity.ok(ApiResponse.success("Observación actualizada exitosamente", observation));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Conflicto de versión")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ApiResponse.error(e.getMessage()));
            } else if (e.getMessage().contains("no encontrada")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("Solo el autor")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(e.getMessage()));
            }
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor"));
        }
    }

    @GetMapping("/session/{sessionId}")
    @Operation(summary = "Obtener observaciones de una sesión")
    public ResponseEntity<ApiResponse<List<ClinicalObservationResponse>>> getObservationsBySession(
            @PathVariable UUID sessionId) {
        try {
            List<ClinicalObservationResponse> observations = observationService.getObservationsBySession(sessionId);
            return ResponseEntity.ok(ApiResponse.success("Observaciones obtenidas exitosamente", observations));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor"));
        }
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Obtener todas las observaciones de un paciente")
    public ResponseEntity<ApiResponse<PaginatedObservationsResponse>> getObservationsByPatient(
            @PathVariable Integer patientId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        try {
            PaginatedObservationsResponse observations = observationService.getObservationsByPatient(
                    patientId, page, limit, sortBy, sortOrder);
            return ResponseEntity.ok(ApiResponse.success("Observaciones obtenidas exitosamente", observations));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor"));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar observación (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteObservation(
            @PathVariable UUID id,
            Principal principal) {
        try {
            observationService.deleteObservation(id, principal.getName());
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("no encontrada")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("Solo el autor")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(e.getMessage()));
            }
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor"));
        }
    }
}