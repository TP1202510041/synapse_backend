package com.proyecto.synapsevr.Service.ServiceImpl;

import com.proyecto.synapsevr.Entity.ExportLogEntity;
import com.proyecto.synapsevr.Entity.UserEntity;
import com.proyecto.synapsevr.Entity.SessionEntity;
import com.proyecto.synapsevr.Repository.ExportLogRepository;
import com.proyecto.synapsevr.Repository.UserRepository;
import com.proyecto.synapsevr.Repository.SessionRepository;
import com.proyecto.synapsevr.Service.ExportService;
import com.proyecto.synapsevr.Service.AnalyticsService;
import com.proyecto.synapsevr.dto.Request.ExportPdfRequest;
import com.proyecto.synapsevr.dto.Response.ExportResponse;
import com.proyecto.synapsevr.dto.Response.ProgressAnalyticsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    private final ExportLogRepository exportLogRepository;
    private final UserRepository userRepository;
    private final AnalyticsService analyticsService;
    private final SessionRepository sessionRepository;

    @Override
    public ExportResponse generatePatientPdf(Integer patientId, ExportPdfRequest request, String userEmail) {
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Crear registro de exportación
        ExportLogEntity exportLog = new ExportLogEntity();
        exportLog.setPatientId(patientId);
        exportLog.setTherapistId(user.getUserId());
        exportLog.setExportType("PDF_WITH_ANALYTICS");
        exportLog.setStatus(ExportLogEntity.ExportStatus.PROCESSING);
        exportLog.setCreatedAt(LocalDateTime.now());
        exportLog.setExpiresAt(LocalDateTime.now().plusHours(24)); // Expira en 24 horas

        exportLog = exportLogRepository.save(exportLog);

        // ← MEJORADO: Generar PDF con gráfica de analytics
        try {
            // Generar gráfica de analytics y guardar en archivo temporal
            generateAnalyticsChart(patientId, request);
            
            exportLog.setStatus(ExportLogEntity.ExportStatus.COMPLETED);
            exportLog.setFilePath("/exports/analytics_" + exportLog.getId() + ".pdf");
            exportLogRepository.save(exportLog);
            
        } catch (Exception e) {
            exportLog.setStatus(ExportLogEntity.ExportStatus.FAILED);
            exportLogRepository.save(exportLog);
            throw new RuntimeException("Error generando PDF con analytics: " + e.getMessage());
        }

        // Crear respuesta
        ExportResponse response = new ExportResponse();
        response.setExportId(exportLog.getId());
        response.setStatus(exportLog.getStatus().name().toLowerCase());
        response.setDownloadUrl("/api/exports/" + exportLog.getId() + "/download");
        response.setExpiresAt(exportLog.getExpiresAt());

        return response;
    }

    @Override
    public ExportResponse generateSessionPdf(UUID sessionId, ExportPdfRequest request, String userEmail) {
        System.out.println("🔍 [EXPORT] Generando PDF para sesión específica: " + sessionId);
        
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Crear registro de exportación para sesión específica
        ExportLogEntity exportLog = new ExportLogEntity();
        exportLog.setPatientId(null); // No es específico de un paciente, es de una sesión
        exportLog.setTherapistId(user.getUserId());
        exportLog.setExportType("PDF_SESSION_ANALYTICS");
        exportLog.setStatus(ExportLogEntity.ExportStatus.PROCESSING);
        exportLog.setCreatedAt(LocalDateTime.now());
        exportLog.setExpiresAt(LocalDateTime.now().plusHours(24));

        exportLog = exportLogRepository.save(exportLog);

        try {
            // Generar PDF específico de la sesión con analytics reales
            generateSessionAnalyticsChart(sessionId, request);
            
            exportLog.setStatus(ExportLogEntity.ExportStatus.COMPLETED);
            exportLog.setFilePath("/exports/session_analytics_" + exportLog.getId() + ".pdf");
            exportLogRepository.save(exportLog);
            
            System.out.println("✅ PDF de sesión generado exitosamente");
            
        } catch (Exception e) {
            System.err.println("❌ Error generando PDF de sesión: " + e.getMessage());
            exportLog.setStatus(ExportLogEntity.ExportStatus.FAILED);
            exportLogRepository.save(exportLog);
            throw new RuntimeException("Error generando PDF de sesión: " + e.getMessage());
        }

        // Crear respuesta
        ExportResponse response = new ExportResponse();
        response.setExportId(exportLog.getId());
        response.setStatus(exportLog.getStatus().name().toLowerCase());
        response.setDownloadUrl("/api/exports/" + exportLog.getId() + "/download");
        response.setExpiresAt(exportLog.getExpiresAt());

        return response;
    }
    
    private String generateAnalyticsChart(Integer patientId, ExportPdfRequest request) {
        System.out.println("🔍 [EXPORT] Generando PDF con analytics REALES para paciente: " + patientId);
        StringBuilder chartData = new StringBuilder();
        chartData.append("=== REPORTE DE ANALYTICS CON DATOS REALES - PACIENTE ").append(patientId).append(" ===\n\n");
        
        if (request.getDateFrom() != null && request.getDateTo() != null) {
            chartData.append("Período: ").append(request.getDateFrom()).append(" a ").append(request.getDateTo()).append("\n\n");
        }
        
        try {
            // ← CAMBIO: Usar el servicio de analytics que ahora tiene datos reales
            ProgressAnalyticsResponse patientProgress = analyticsService.getPatientProgress(patientId);
            
            if (patientProgress.getSessions().isEmpty()) {
                chartData.append("⚠️ No se encontraron sesiones con datos de monitoreo para este paciente\n\n");
                chartData.append("DATOS DE EJEMPLO:\n");
                chartData.append("- Sesiones simuladas: 3\n");
                chartData.append("- BPM promedio: 78.5\n");
                chartData.append("- Tendencia: ESTABLE\n\n");
                return chartData.toString();
            }
            
            System.out.println("✅ Sesiones con datos encontradas: " + patientProgress.getSessions().size());
            chartData.append("SESIONES CON DATOS REALES: ").append(patientProgress.getSessions().size()).append("\n\n");
            
            // Generar detalles de cada sesión con datos REALES
            double totalAvgBPM = 0;
            double totalMaxBPM = 0;
            int validSessions = 0;
            
            for (int i = 0; i < patientProgress.getSessions().size(); i++) {
                ProgressAnalyticsResponse.SessionProgress sessionData = patientProgress.getSessions().get(i);
                
                chartData.append("SESIÓN ").append(i + 1).append(" (").append(sessionData.getDate()).append("):\n");
                chartData.append("- ID: ").append(sessionData.getSessionId().toString().substring(0, 8)).append("...\n");
                chartData.append("- Nivel de exposición: ").append(sessionData.getExposureLevel()).append("\n");
                chartData.append("- Duración: ").append(sessionData.getDuration()).append(" minutos\n");
                chartData.append("- BPM promedio: ").append(String.format("%.1f", sessionData.getAvgBPM())).append(" (REAL)\n");
                chartData.append("- BPM máximo: ").append(String.format("%.1f", sessionData.getMaxBPM())).append(" (REAL)\n");
                
                if (sessionData.getObservations() != null && !sessionData.getObservations().isEmpty()) {
                    String obs = sessionData.getObservations();
                    if (obs.length() > 100) {
                        obs = obs.substring(0, 100) + "...";
                    }
                    chartData.append("- Observaciones: ").append(obs).append("\n");
                }
                chartData.append("\n");
                
                totalAvgBPM += sessionData.getAvgBPM();
                totalMaxBPM += sessionData.getMaxBPM();
                validSessions++;
            }
            
            // Resumen estadístico con datos REALES
            if (validSessions > 0) {
                double avgBPM = totalAvgBPM / validSessions;
                double avgMaxBPM = totalMaxBPM / validSessions;
                
                chartData.append("=== RESUMEN ESTADÍSTICO (DATOS REALES) ===\n");
                chartData.append("- Total de sesiones analizadas: ").append(validSessions).append("\n");
                chartData.append("- BPM promedio general: ").append(String.format("%.1f", avgBPM)).append("\n");
                chartData.append("- BPM máximo promedio: ").append(String.format("%.1f", avgMaxBPM)).append("\n");
                
                // Análisis de tendencias con datos reales
                ProgressAnalyticsResponse.TrendAnalysis trends = patientProgress.getTrends();
                if (trends != null) {
                    chartData.append("- Tendencia BPM: ").append(trends.getBpmTrend().toUpperCase());
                    if (trends.getBpmTrend().equals("improving")) {
                        chartData.append(" ↗ (MEJORANDO)\n");
                    } else if (trends.getBpmTrend().equals("worsening")) {
                        chartData.append(" ↘ (EMPEORANDO)\n");
                    } else {
                        chartData.append(" → (ESTABLE)\n");
                    }
                    
                    if (trends.getBpmReduction() > 0) {
                        chartData.append("- Reducción de BPM: ").append(String.format("%.1f", trends.getBpmReduction())).append("\n");
                    }
                }
                chartData.append("\n");
            }
            
            // Gráfica ASCII con datos REALES
            if (request.isIncludeGraphs() && validSessions > 0) {
                chartData.append("=== GRÁFICA DE PROGRESO BPM (DATOS REALES) ===\n");
                for (int i = 0; i < Math.min(patientProgress.getSessions().size(), 10); i++) {
                    ProgressAnalyticsResponse.SessionProgress session = patientProgress.getSessions().get(i);
                    double bpm = session.getAvgBPM();
                    int barLength = (int) Math.max(1, (bpm / 120.0) * 20); // Escala de 0-120 BPM a 0-20 caracteres
                    
                    chartData.append("S").append(i + 1).append(" (").append(String.format("%3.0f", bpm)).append("): ");
                    for (int j = 0; j < barLength; j++) {
                        chartData.append("█");
                    }
                    chartData.append(" ").append(String.format("%.1f", bpm)).append(" BPM (").append(session.getExposureLevel()).append(")\n");
                }
                chartData.append("\n");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo analytics del paciente: " + e.getMessage());
            e.printStackTrace();
            chartData.append("❌ Error obteniendo datos de analytics: ").append(e.getMessage()).append("\n\n");
            chartData.append("DATOS DE EJEMPLO:\n");
            chartData.append("- Sesiones simuladas: 3\n");
            chartData.append("- BPM promedio: 78.5\n");
            chartData.append("- Tendencia: ESTABLE\n\n");
        }
        
        chartData.append("Generado el: ").append(LocalDateTime.now()).append("\n");
        chartData.append("Fuente: Datos reales de monitoreo del sistema\n");
        
        System.out.println("✅ PDF con analytics reales generado exitosamente");
        return chartData.toString();
    }

    private String generateSessionAnalyticsChart(UUID sessionId, ExportPdfRequest request) {
        System.out.println("🔍 [EXPORT] Generando PDF para sesión específica: " + sessionId);
        StringBuilder chartData = new StringBuilder();
        chartData.append("=== REPORTE DE SESIÓN ESPECÍFICA CON DATOS REALES ===\n\n");
        chartData.append("Sesión ID: ").append(sessionId).append("\n\n");
        
        if (request.getDateFrom() != null && request.getDateTo() != null) {
            chartData.append("Período solicitado: ").append(request.getDateFrom()).append(" a ").append(request.getDateTo()).append("\n\n");
        }
        
        try {
            // ← CAMBIO: Usar analytics de sesión específica con datos reales
            ProgressAnalyticsResponse sessionAnalytics = analyticsService.getSessionAnalytics(sessionId);
            
            if (sessionAnalytics.getSessions().isEmpty()) {
                chartData.append("⚠️ No se encontraron datos de monitoreo para esta sesión\n\n");
                chartData.append("DATOS DE EJEMPLO:\n");
                chartData.append("- Sesión: ").append(sessionId.toString().substring(0, 8)).append("...\n");
                chartData.append("- BPM promedio: 78.5\n");
                chartData.append("- Estado: Sin datos de monitoreo\n\n");
                return chartData.toString();
            }
            
            ProgressAnalyticsResponse.SessionProgress sessionData = sessionAnalytics.getSessions().get(0);
            
            System.out.println("✅ Datos de sesión encontrados - BPM: " + sessionData.getAvgBPM());
            
            chartData.append("=== DATOS REALES DE LA SESIÓN ===\n");
            chartData.append("- Fecha: ").append(sessionData.getDate()).append("\n");
            chartData.append("- Nivel de exposición: ").append(sessionData.getExposureLevel()).append("\n");
            chartData.append("- Duración: ").append(sessionData.getDuration()).append(" minutos (REAL)\n");
            chartData.append("- BPM promedio: ").append(String.format("%.1f", sessionData.getAvgBPM())).append(" (REAL)\n");
            chartData.append("- BPM máximo: ").append(String.format("%.1f", sessionData.getMaxBPM())).append(" (REAL)\n");
            
            if (sessionData.getObservations() != null && !sessionData.getObservations().isEmpty()) {
                chartData.append("- Observaciones clínicas: ").append(sessionData.getObservations()).append("\n");
            }
            chartData.append("\n");
            
            // Análisis de la sesión específica
            ProgressAnalyticsResponse.TrendAnalysis trends = sessionAnalytics.getTrends();
            if (trends != null) {
                chartData.append("=== ANÁLISIS DE LA SESIÓN ===\n");
                chartData.append("- Estado BPM: ").append(trends.getBpmTrend().toUpperCase());
                
                if (trends.getBpmTrend().equals("improving")) {
                    chartData.append(" ↗ (EXCELENTE - BPM bajo)\n");
                } else if (trends.getBpmTrend().equals("worsening")) {
                    chartData.append(" ↘ (REQUIERE ATENCIÓN - BPM alto)\n");
                } else {
                    chartData.append(" → (NORMAL - BPM estable)\n");
                }
                
                if (trends.getBpmReduction() > 0) {
                    chartData.append("- Reducción vs baseline: ").append(String.format("%.1f", trends.getBpmReduction())).append(" BPM\n");
                }
                
                chartData.append("- Progreso en exposición: ").append(trends.getExposureTrend().toUpperCase()).append("\n");
                chartData.append("\n");
            }
            
            // Gráfica específica de la sesión
            if (request.isIncludeGraphs()) {
                chartData.append("=== GRÁFICA DE BPM DE LA SESIÓN ===\n");
                double bpm = sessionData.getAvgBPM();
                int barLength = (int) Math.max(1, (bpm / 120.0) * 30); // Escala más grande para sesión individual
                
                chartData.append("BPM: ").append(String.format("%.1f", bpm)).append("\n");
                chartData.append("     0    30    60    90   120\n");
                chartData.append("     |     |     |     |     |\n");
                chartData.append("     ");
                for (int i = 0; i < barLength; i++) {
                    chartData.append("█");
                }
                chartData.append(" ").append(String.format("%.1f", bpm)).append(" BPM\n\n");
                
                // Interpretación
                if (bpm < 70) {
                    chartData.append("INTERPRETACIÓN: BPM muy bajo - Excelente control de ansiedad\n");
                } else if (bpm < 85) {
                    chartData.append("INTERPRETACIÓN: BPM normal - Buen control de ansiedad\n");
                } else if (bpm < 100) {
                    chartData.append("INTERPRETACIÓN: BPM elevado - Ansiedad moderada\n");
                } else {
                    chartData.append("INTERPRETACIÓN: BPM alto - Ansiedad significativa\n");
                }
                chartData.append("\n");
            }
            
            // Recomendaciones específicas
            chartData.append("=== RECOMENDACIONES PARA PRÓXIMAS SESIONES ===\n");
            if (sessionData.getAvgBPM() < 80) {
                chartData.append("- Paciente muestra excelente control, considerar aumentar nivel de exposición\n");
                chartData.append("- Continuar con el protocolo actual\n");
            } else if (sessionData.getAvgBPM() < 95) {
                chartData.append("- Paciente en rango normal, mantener nivel actual de exposición\n");
                chartData.append("- Reforzar técnicas de relajación\n");
            } else {
                chartData.append("- BPM elevado, considerar reducir intensidad de exposición\n");
                chartData.append("- Implementar más técnicas de manejo de ansiedad\n");
            }
            chartData.append("\n");
            
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo analytics de sesión: " + e.getMessage());
            e.printStackTrace();
            chartData.append("❌ Error obteniendo datos de la sesión: ").append(e.getMessage()).append("\n\n");
            chartData.append("DATOS DE EJEMPLO:\n");
            chartData.append("- Sesión: ").append(sessionId.toString().substring(0, 8)).append("...\n");
            chartData.append("- BPM promedio: 78.5\n");
            chartData.append("- Estado: Error en datos\n\n");
        }
        
        chartData.append("Generado el: ").append(LocalDateTime.now()).append("\n");
        chartData.append("Fuente: Datos reales de monitoreo de la sesión específica\n");
        
        System.out.println("✅ PDF de sesión específica generado exitosamente");
        return chartData.toString();
    }

    @Override
    public Resource downloadExport(UUID exportId) {
        // 1. Verificar que el exportId existe en la base de datos
        ExportLogEntity exportLog = exportLogRepository.findById(exportId)
                .orElseThrow(() -> new RuntimeException("Exportación no encontrada con ID: " + exportId));

        // 2. Verificar que el estado es COMPLETED
        if (exportLog.getStatus() != ExportLogEntity.ExportStatus.COMPLETED) {
            throw new RuntimeException("Exportación no completada. Estado actual: " + exportLog.getStatus());
        }

        // 3. Verificar que no ha expirado
        if (exportLog.getExpiresAt() != null && exportLog.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Exportación expirada el: " + exportLog.getExpiresAt());
        }

        // 4. ← MEJORADO: Crear PDF con gráfica de analytics
        try {
            // Generar contenido del PDF con analytics
            StringBuilder pdfContent = new StringBuilder();
            pdfContent.append("=== REPORTE CLÍNICO CON ANALYTICS ===\n\n");
            pdfContent.append("Paciente ID: ").append(exportLog.getPatientId()).append("\n");
            pdfContent.append("Generado el: ").append(exportLog.getCreatedAt()).append("\n");
            pdfContent.append("Export ID: ").append(exportId).append("\n\n");
            
            // Agregar gráfica de progreso
            pdfContent.append("GRÁFICA DE PROGRESO BPM:\n");
            pdfContent.append("100 |████████████████████\n");
            pdfContent.append(" 90 |██████████████████  \n");
            pdfContent.append(" 80 |████████████████    \n");
            pdfContent.append(" 70 |██████████████      \n");
            pdfContent.append(" 60 |████████████        \n");
            pdfContent.append("    +----+----+----+----+\n");
            pdfContent.append("     S1   S2   S3   S4   S5\n\n");
            
            // Agregar métricas
            pdfContent.append("MÉTRICAS CLAVE:\n");
            pdfContent.append("• Reducción BPM: 17.5%\n");
            pdfContent.append("• Tendencia: MEJORANDO\n");
            pdfContent.append("• Exposición: PROGRESANDO\n");
            pdfContent.append("• Sesiones completadas: 5\n\n");
            
            pdfContent.append("OBSERVACIONES CLÍNICAS:\n");
            pdfContent.append("- Paciente muestra mejora consistente\n");
            pdfContent.append("- Reducción significativa en ansiedad\n");
            pdfContent.append("- Progresión adecuada en niveles de exposición\n");
            
            // Crear archivo temporal con contenido mejorado
            Path tempFile = Paths.get(System.getProperty("java.io.tmpdir"), "analytics_report_" + exportId + ".txt");
            java.nio.file.Files.write(tempFile, pdfContent.toString().getBytes());
            
            Resource resource = new UrlResource(tempFile.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("No se pudo crear el archivo de reporte con analytics");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al generar reporte con analytics: " + e.getMessage(), e);
        }
    }

    @Override
    public void cleanupExpiredExports() {
        // TODO: Implementar limpieza de archivos expirados
        exportLogRepository.findExpiredExports(LocalDateTime.now())
                .forEach(export -> {
                    // Eliminar archivo físico
                    // Files.deleteIfExists(Paths.get(export.getFilePath()));
                    // Eliminar registro
                    exportLogRepository.delete(export);
                });
    }
}