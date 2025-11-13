package com.proyecto.synapsevr.dto.Response;

import lombok.Data;

import java.util.List;

@Data
public class PaginatedMonitoringObservationsResponse {

    private List<MonitoringObservationResponse> observations;
    private PaginationInfo pagination;

    @Data
    public static class PaginationInfo {
        private int page;
        private int limit;
        private long total;
        private int totalPages;
    }
}