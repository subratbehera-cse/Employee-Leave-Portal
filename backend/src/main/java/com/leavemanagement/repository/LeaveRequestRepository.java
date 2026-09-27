package com.leavemanagement.repository;

import com.leavemanagement.entity.LeaveRequest;
import com.leavemanagement.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByUserIdOrderByAppliedAtDesc(Long userId);

    List<LeaveRequest> findByUserIdInOrderByAppliedAtDesc(List<Long> userIds);

    List<LeaveRequest> findAllByOrderByAppliedAtDesc();

    long countByUserIdAndStatus(Long userId, LeaveStatus status);

    long countByStatus(LeaveStatus status);

    long countByUserIdInAndStatus(List<Long> userIds, LeaveStatus status);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.user.id = :userId " +
            "AND lr.status IN ('PENDING', 'APPROVED') " +
            "AND lr.startDate <= :endDate AND lr.endDate >= :startDate")
    List<LeaveRequest> findOverlapping(@Param("userId") Long userId,
                                        @Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.user.id = :userId " +
            "AND lr.status = 'APPROVED' AND lr.startDate >= CURRENT_DATE " +
            "ORDER BY lr.startDate ASC")
    List<LeaveRequest> findUpcomingApproved(@Param("userId") Long userId);
}
