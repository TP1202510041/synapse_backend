package com.proyecto.synapsevr.dto.Response;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class SessionComparisonResponse {

    private SessionData session1;
    private SessionData session2;
    private ComparisonAnalysis comparison;

    @Data
    public static class SessionData {
        private UUID sessionId;
        private LocalDate sessionDate;
        private String description;
        private String exposureLevel;
        private Integer patientId;
        private String patientName;
        
        // Datos de monitoreo
        private MonitoringData monitoring;
        
        // Observaciones
        private List<String> observations;
    }

    @Data
    public static class MonitoringData {
        private Integer duration; // en minutos
        private Double avgHeartRate;
        private Double maxHeartRate;
        private Double minHeartRate;
        private Integer totalRecords;
        private Double heartRateVariability; // Desviación estándar
        
        // Datos adicionales calculados
        private Double avgBPM;
        private Double maxBPM;
        private Double recoveryTime; // Tiempo para volver al ritmo normal
    }

    @Data
    public static class ComparisonAnalysis {
        private HeartRateComparison heartRate;
        private DurationComparison duration;
        private PerformanceComparison performance;
        private String overallImprovement;
        private List<String> insights;
    }

    @Data
    public static class HeartRateComparison {
        private Double avgDifference; // session2 - session1
        private Double maxDifference;
        private String avgTrend; // "IMPROVED", "WORSENED", "STABLE"
        private String maxTrend;
        private Double improvementPercentage;
    }

    @Data
    public static class DurationComparison {
        private Integer difference; // session2 - session1 (en minutos)
        private String trend; // "LONGER", "SHORTER", "SAME"
        private Double improvementPercentage;
    }

    @Data
    public static class PerformanceComparison {
        private String overallTrend; // "IMPROVED", "WORSENED", "STABLE"
        private Double stabilityImprovement; // Mejora en variabilidad del ritmo cardíaco
        private Integer recordsComparison; // Diferencia en número de registros
        private String enduranceImprovement; // Mejora en resistencia
    }
}