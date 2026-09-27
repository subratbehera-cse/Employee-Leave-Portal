package com.leavemanagement.service;

import com.leavemanagement.dto.response.AuditLogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditLogService {
    void log(Long userId, String action, String entityType, Long entityId, String description);
    Page<AuditLogResponse> getLogs(Pageable pageable);
}
