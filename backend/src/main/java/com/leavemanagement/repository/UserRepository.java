package com.leavemanagement.repository;

import com.leavemanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByEmployeeCode(String employeeCode);
    List<User> findByManagerId(Long managerId);
    List<User> findByDepartmentId(Long departmentId);
    long countByDepartmentId(Long departmentId);
}
