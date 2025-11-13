package com.proyecto.synapsevr.dto.Response;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class ProgressAnalyticsResponse {

    private List<SessionProgress> sessions;
    private TrendAnalysis trends;

    @Data
    public static class SessionProgress {
        private UUID sessionId;
        private LocalDate date;
        private Double avgBPM;
        private Double maxBPM;
        private Integer duration;
        private String exposureLevel;
        private String observations;
    }

    @Data
    public static class TrendAnalysis {
        private String bpmTrend; // "improving", "stable", "worsening"
        private String exposureTrend; // "progressing", "stable", "regressing"
        private Double bpmReduction;
        private List<String> exposureProgression;
    }
}