package com.proyecto.synapsevr.dto.Response;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PatientMetricsResponse {

    private AggregatedMetrics aggregatedMetrics;
    private List<EvolutionData> evolutionData;
    private TrendAnalysis trendAnalysis;

    @Data
    public static class AggregatedMetrics {
        private Integer totalSessions;
        private Double avgBPM;
        private Double avgSessionDuration;
        private Double bpmReduction;
        private List<String> exposureProgression;
    }

    @Data
    public static class EvolutionData {
        private Integer sessionNumber;
        private LocalDate date;
        private Double avgBPM;
        private Double maxBPM;
        private String exposureLevel;
    }

    @Data
    public static class TrendAnalysis {
        private String bpmTrend; // "improving", "stable", "worsening"
        private Double trendPercentage;
        private List<Milestone> milestones;
    }

    @Data
    public static class Milestone {
        private LocalDate date;
        private String description;
        private String type; // "improvement", "setback", "milestone"
    }
}