package com.proyecto.synapsevr.dto.Request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class MonitoringRecordRequest {

    private UUID sessionId;
    private Integer patientId;
    private Long startTime;
    private Long endTime;
    private Long startTimeLocal;
    private Long endTimeLocal;
    private Integer duration;
    private Integer totalRecords;
    private BigDecimal avgHeartRate;
    private BigDecimal minHeartRate;
    private BigDecimal maxHeartRate;
    
    // JSON completo de los datos para MongoDB
    private List<Object> heartRateRecords;
    
    public MonitoringRecordRequest(UUID sessionId, Integer patientId, Long startTime, Long endTime, 
                                 Long startTimeLocal, Long endTimeLocal, Integer duration, Integer totalRecords,
                                 BigDecimal avgHeartRate, BigDecimal minHeartRate, BigDecimal maxHeartRate,
                                 List<Object> heartRateRecords) {
        this.sessionId = sessionId;
        this.patientId = patientId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.startTimeLocal = startTimeLocal;
        this.endTimeLocal = endTimeLocal;
        this.duration = duration;
        this.totalRecords = totalRecords;
        this.avgHeartRate = avgHeartRate;
        this.minHeartRate = minHeartRate;
        this.maxHeartRate = maxHeartRate;
        this.heartRateRecords = heartRateRecords;
    }
}