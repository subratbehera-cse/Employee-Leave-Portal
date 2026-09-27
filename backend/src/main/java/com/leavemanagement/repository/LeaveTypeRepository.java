package com.leavemanagement.repository;

import com.leavemanagement.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {
    boolean existsByName(String name);
    Optional<LeaveType> findByName(String name);
    List<LeaveType> findByIsActiveTrue();
}
