package com.proyecto.synapsevr.Controller;

import com.proyecto.synapsevr.dto.Request.ExportPdfRequest;
import com.proyecto.synapsevr.dto.Response.ApiResponse;
import com.proyecto.synapsevr.dto.Response.ExportResponse;
import com.proyecto.synapsevr.Service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/exports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Export", description = "API para exportación de historiales clínicos")
public class ExportController {

    private final ExportService exportService;

    @PostMapping("/patient/{patientId}/pdf")
    @Operation(summary = "Generar PDF del historial completo del paciente")
    public ResponseEntity<ApiResponse<ExportResponse>> generatePatientPdf(
            @PathVariable Integer patientId,
            @RequestBody ExportPdfRequest request,
            Principal principal) {
        try {
            System.out.println("🔍 [EXPORT] Generando PDF para paciente: " + patientId);
            System.out.println("👤 Usuario: " + principal.getName());
            
            ExportResponse export = exportService.generatePatientPdf(patientId, request, principal.getName());
            return ResponseEntity.ok(ApiResponse.success("Exportación iniciada", export));
        } catch (Exception e) {
            System.err.println("❌ Error generando PDF del paciente: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor: " + e.getMessage()));
        }
    }

    @PostMapping("/session/{sessionId}/pdf")
    @Operation(summary = "Generar PDF de una sesión específica con analytics")
    public ResponseEntity<ApiResponse<ExportResponse>> generateSessionPdf(
            @PathVariable UUID sessionId,
            @RequestBody ExportPdfRequest request,
            Principal principal) {
        try {
            System.out.println("🔍 [EXPORT] Generando PDF para sesión: " + sessionId);
            System.out.println("👤 Usuario: " + principal.getName());
            
            ExportResponse export = exportService.generateSessionPdf(sessionId, request, principal.getName());
            return ResponseEntity.ok(ApiResponse.success("PDF de sesión generado exitosamente", export));
        } catch (Exception e) {
            System.err.println("❌ Error generando PDF de sesión: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor: " + e.getMessage()));
        }
    }

    @GetMapping("/{exportId}/download")
    @Operation(summary = "Descargar PDF generado")
    public ResponseEntity<?> downloadExport(@PathVariable UUID exportId, Principal principal) {
        try {
            System.out.println("🔍 [EXPORT] Descargando PDF: " + exportId);
            System.out.println("👤 Usuario autenticado: " + principal.getName());
            
            Resource resource = exportService.downloadExport(exportId);
            
            System.out.println("✅ PDF encontrado, iniciando descarga");
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"historial_clinico_" + exportId + ".pdf\"")
                    .body(resource);
        } catch (RuntimeException e) {
            // Log del error para debugging
            System.err.println("❌ Error descargando PDF " + exportId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Error al descargar PDF: " + e.getMessage()));
        } catch (Exception e) {
            System.err.println("❌ Error interno descargando PDF " + exportId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error interno del servidor"));
        }
    }
}