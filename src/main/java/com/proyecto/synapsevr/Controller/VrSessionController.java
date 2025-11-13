package com.proyecto.synapsevr.Controller;

import com.proyecto.synapsevr.dto.Request.VrSessionRequest;
import com.proyecto.synapsevr.dto.Response.ApiResponse;
import com.proyecto.synapsevr.dto.Response.FilteredSessionsResponse;
import com.proyecto.synapsevr.dto.Response.VrSessionResponse;
import com.proyecto.synapsevr.Service.VrSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/sessions/vr")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "VR Sessions", description = "API para gestión de sesiones de realidad virtual")
public class VrSessionController {

    private final VrSessionService vrSessionService;

    @PostMapping
    @Operation(summary = "Crear nueva sesión VR")
    public ResponseEntity<ApiResponse<VrSessionResponse>> createVrSession(
            @Valid @RequestBody VrSessionRequest request,
            Principal principal) {
        try {
            VrSessionResponse vrSession = vrSessionService.createVrSession(request, principal.getName());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Sesión VR creada exitosamente", vrSession));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor"));
        }
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Obtener historial específico de sesiones VR")
    public ResponseEntity<ApiResponse<FilteredSessionsResponse>> getVrSessionsByPatient(
            @PathVariable Integer patientId,
            @RequestParam(required = false) String vrScenario,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        try {
            FilteredSessionsResponse sessions = vrSessionService.getVrSessionsByPatient(
                    patientId, vrScenario, dateFrom, dateTo, page, limit);
            return ResponseEntity.ok(ApiResponse.success("Sesiones VR obtenidas exitosamente", sessions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor"));
        }
    }

    @GetMapping("/scenario/{scenario}")
    @Operation(summary = "Obtener sesiones por escenario VR")
    public ResponseEntity<ApiResponse<List<VrSessionResponse>>> getVrSessionsByScenario(
            @PathVariable String scenario) {
        try {
            List<VrSessionResponse> sessions = vrSessionService.getVrSessionsByScenario(scenario);
            return ResponseEntity.ok(ApiResponse.success("Sesiones obtenidas exitosamente", sessions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor"));
        }
    }

    @GetMapping("/device/{device}")
    @Operation(summary = "Obtener sesiones por dispositivo VR")
    public ResponseEntity<ApiResponse<List<VrSessionResponse>>> getVrSessionsByDevice(
            @PathVariable String device) {
        try {
            List<VrSessionResponse> sessions = vrSessionService.getVrSessionsByDevice(device);
            return ResponseEntity.ok(ApiResponse.success("Sesiones obtenidas exitosamente", sessions));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor"));
        }
    }
}