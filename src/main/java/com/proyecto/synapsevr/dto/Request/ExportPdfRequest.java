package com.proyecto.synapsevr.dto.Request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ExportPdfRequest {

    private boolean includeGraphs = true;
    private LocalDate dateFrom;
    private LocalDate dateTo;
}