package com.proyecto.synapsevr.Service;

import com.proyecto.synapsevr.dto.Response.ProgressAnalyticsResponse;
import com.proyecto.synapsevr.dto.Response.PatientMetricsResponse;
import com.proyecto.synapsevr.dto.Response.SessionComparisonResponse;

import java.util.List;
import java.util.UUID;

public interface AnalyticsService {

    ProgressAnalyticsResponse getPatientProgress(Integer patientId);

    ProgressAnalyticsResponse compareSessionsProgress(List<UUID> sessionIds);

    ProgressAnalyticsResponse getSessionAnalytics(UUID sessionId);

    PatientMetricsResponse getPatientMetrics(Integer patientId);

    SessionComparisonResponse compareSessionsDetailed(UUID session1Id, UUID session2Id);
}