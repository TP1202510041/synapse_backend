package com.proyecto.synapsevr.dto.Response;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class FilteredSessionsResponse {

    private List<SessionSummary> sessions;
    private PaginationInfo pagination;
    private FilterInfo filters;

    @Data
    public static class SessionSummary {
        private UUID id;
        private LocalDate sessionDate;
        private String description;
        private String exposureLevel;
        private Integer duration;
        private String status;
    }

    @Data
    public static class PaginationInfo {
        private int page;
        private int limit;
        private long total;
        private int totalPages;
    }

    @Data
    public static class FilterInfo {
        private List<String> exposureLevel;
        private String dateRange;
    }
}