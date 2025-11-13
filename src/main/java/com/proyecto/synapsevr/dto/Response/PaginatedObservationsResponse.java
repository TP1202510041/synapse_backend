package com.proyecto.synapsevr.dto.Response;

import lombok.Data;

import java.util.List;

@Data
public class PaginatedObservationsResponse {

    private List<ClinicalObservationResponse> observations;
    private PaginationInfo pagination;

    @Data
    public static class PaginationInfo {
        private int page;
        private int limit;
        private long total;
        private int totalPages;
    }
}