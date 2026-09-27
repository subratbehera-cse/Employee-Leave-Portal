package com.leavemanagement.service.impl;

import com.leavemanagement.dto.response.AuditLogResponse;
import com.leavemanagement.entity.AuditLog;
import com.leavemanagement.entity.User;
import com.leavemanagement.mapper.AuditLogMapper;
import com.leavemanagement.repository.AuditLogRepository;
import com.leavemanagement.repository.UserRepository;
import com.leavemanagement.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final AuditLogMapper auditLogMapper;

    @Override
    @Transactional
    public void log(Long userId, String action, String entityType, Long entityId, String description) {
        User user = userId != null ? userRepository.findById(userId).orElse(null) : null;
        AuditLog auditLog = AuditLog.builder()
                .user(user)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .description(description)
                .build();
        auditLogRepository.save(auditLog);
    }

    @Override
    public Page<AuditLogResponse> getLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByCreatedAtDesc(pageable).map(auditLogMapper::toResponse);
    }
}
