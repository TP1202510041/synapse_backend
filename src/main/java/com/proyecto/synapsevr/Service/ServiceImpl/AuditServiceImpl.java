package com.proyecto.synapsevr.Service.ServiceImpl;

import com.proyecto.synapsevr.Entity.AuditLogEntity;
import com.proyecto.synapsevr.Repository.AuditLogRepository;
import com.proyecto.synapsevr.Service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;

    @Override
    public void logAction(Integer userId, String entityType, String entityId, String fieldName,
            String oldValue, String newValue, AuditLogEntity.AuditAction action,
            String ipAddress, String userAgent) {
        AuditLogEntity auditLog = new AuditLogEntity();
        auditLog.setUserId(userId);
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setFieldName(fieldName);
        auditLog.setOldValue(oldValue);
        auditLog.setNewValue(newValue);
        auditLog.setAction(action);
        auditLog.setIpAddress(ipAddress);
        auditLog.setUserAgent(userAgent);

        auditLogRepository.save(auditLog);
    }

    @Override
    public List<AuditLogEntity> getEntityAuditHistory(String entityType, String entityId) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByTimestampDesc(entityType, entityId);
    }

    @Override
    public List<AuditLogEntity> getUserAuditHistory(Integer userId, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        return auditLogRepository.findByUserIdOrderByTimestampDesc(userId, pageable).getContent();
    }
}