package com.proyecto.synapsevr.dto.Request;

import lombok.Data;

import java.util.UUID;

@Data
public class SessionComparisonRequest {
    private UUID session1Id;
    private UUID session2Id;
    
    // Constructor vacío
    public SessionComparisonRequest() {}
    
    // Constructor con parámetros
    public SessionComparisonRequest(UUID session1Id, UUID session2Id) {
        this.session1Id = session1Id;
        this.session2Id = session2Id;
    }
}