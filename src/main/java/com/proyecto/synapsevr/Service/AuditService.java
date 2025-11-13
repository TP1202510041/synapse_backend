package com.proyecto.synapsevr.Service;

import com.proyecto.synapsevr.Entity.AuditLogEntity;

import java.util.List;

public interface AuditService {

    void logAction(Integer userId, String entityType, String entityId, String fieldName, 
                   String oldValue, String newValue, AuditLogEntity.AuditAction action, 
                   String ipAddress, String userAgent);

    List<AuditLogEntity> getEntityAuditHistory(String entityType, String entityId);

    List<AuditLogEntity> getUserAuditHistory(Integer userId, int page, int limit);
}