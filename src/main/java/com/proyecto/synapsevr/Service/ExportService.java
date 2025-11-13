package com.proyecto.synapsevr.Service;

import com.proyecto.synapsevr.dto.Request.ExportPdfRequest;
import com.proyecto.synapsevr.dto.Response.ExportResponse;
import org.springframework.core.io.Resource;

import java.util.UUID;

public interface ExportService {

    ExportResponse generatePatientPdf(Integer patientId, ExportPdfRequest request, String userEmail);

    ExportResponse generateSessionPdf(UUID sessionId, ExportPdfRequest request, String userEmail);

    Resource downloadExport(UUID exportId);

    void cleanupExpiredExports();
}