package com.proyecto.synapsevr.Controller;

import com.proyecto.synapsevr.dto.Response.ApiResponse;
import com.proyecto.synapsevr.dto.Response.ProgressAnalyticsResponse;
import com.proyecto.synapsevr.dto.Response.PatientMetricsResponse;
import com.proyecto.synapsevr.dto.Response.SessionComparisonResponse;
import com.proyecto.synapsevr.dto.Request.SessionComparisonRequest;
import com.proyecto.synapsevr.Service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Analytics", description = "API para análisis y métricas de progreso")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/patient/{patientId}/progress")
    @Operation(summary = "Obtener datos para gráfica de progreso terapéutico")
    public ResponseEntity<ApiResponse<ProgressAnalyticsResponse>> getPatientProgress(
            @PathVariable Integer patientId) {
        try {
            ProgressAnalyticsResponse progress = analyticsService.getPatientProgress(patientId);
            return ResponseEntity.ok(ApiResponse.success("Progreso obtenido exitosamente", progress));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor"));
        }
    }

    @PostMapping("/session-analytics")
    @Operation(summary = "Obtener estadísticas de monitoreo de una sesión específica")
    public ResponseEntity<ApiResponse<ProgressAnalyticsResponse>> getSessionAnalytics(
            @RequestBody SessionAnalyticsRequest request) {
        try {
            // ← CAMBIO: Ahora muestra estadísticas de UNA sesión específica
            ProgressAnalyticsResponse analytics = analyticsService.getSessionAnalytics(request.getSessionId());
            return ResponseEntity.ok(ApiResponse.success("Estadísticas de sesión obtenidas exitosamente", analytics));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor"));
        }
    }

    @GetMapping("/patient/{patientId}/metrics")
    @Operation(summary = "Obtener métricas agregadas y evolución del paciente")
    public ResponseEntity<ApiResponse<PatientMetricsResponse>> getPatientMetrics(
            @PathVariable Integer patientId) {
        try {
            PatientMetricsResponse metrics = analyticsService.getPatientMetrics(patientId);
            return ResponseEntity.ok(ApiResponse.success("Métricas obtenidas exitosamente", metrics));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor"));
        }
    }

    @GetMapping("/session/{sessionId}/analytics")
    @Operation(summary = "Obtener estadísticas de monitoreo de una sesión específica")
    public ResponseEntity<ApiResponse<ProgressAnalyticsResponse>> getSessionAnalytics(
            @PathVariable UUID sessionId) {
        try {
            ProgressAnalyticsResponse analytics = analyticsService.getSessionAnalytics(sessionId);
            return ResponseEntity.ok(ApiResponse.success("Estadísticas de sesión obtenidas exitosamente", analytics));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor"));
        }
    }

    // DTO para analytics de sesión específica
    public static class SessionAnalyticsRequest {
        private UUID sessionId;

        public UUID getSessionId() {
            return sessionId;
        }

        public void setSessionId(UUID sessionId) {
            this.sessionId = sessionId;
        }
    }

    @PostMapping("/sessions/compare")
    @Operation(summary = "Comparar dos sesiones específicas con análisis detallado")
    public ResponseEntity<ApiResponse<SessionComparisonResponse>> compareSessionsDetailed(
            @RequestBody SessionComparisonRequest request) {
        try {
            SessionComparisonResponse comparison = analyticsService.compareSessionsDetailed(
                    request.getSession1Id(), request.getSession2Id());
            return ResponseEntity.ok(ApiResponse.success("Comparación de sesiones completada exitosamente", comparison));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor"));
        }
    }

    // DTO interno para el request de comparación (mantener compatibilidad)
    public static class CompareSessionsRequest {
        private List<UUID> sessionIds;

        public List<UUID> getSessionIds() {
            return sessionIds;
        }

        public void setSessionIds(List<UUID> sessionIds) {
            this.sessionIds = sessionIds;
        }
    }
}