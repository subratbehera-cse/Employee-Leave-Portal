package com.leavemanagement.config;

import com.leavemanagement.entity.*;
import com.leavemanagement.enums.EmployeeStatus;
import com.leavemanagement.enums.Role;
import com.leavemanagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

/**
 * Seeds baseline reference data (departments, leave types, an admin user) on first boot,
 * so the application is usable immediately without manual SQL. Idempotent: skips seeding
 * if data already exists.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final UserRepository userRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (departmentRepository.count() == 0) {
            departmentRepository.saveAll(List.of(
                    Department.builder().name("Engineering").description("Product engineering and platform teams").build(),
                    Department.builder().name("Human Resources").description("People operations and recruitment").build(),
                    Department.builder().name("Sales").description("Sales and business development").build()
            ));
        }

        if (leaveTypeRepository.count() == 0) {
            leaveTypeRepository.saveAll(List.of(
                    LeaveType.builder().name("Annual Leave").description("Planned yearly vacation leave").defaultDays(20).isActive(true).build(),
                    LeaveType.builder().name("Sick Leave").description("Medical and health-related leave").defaultDays(10).isActive(true).build(),
                    LeaveType.builder().name("Casual Leave").description("Short-notice personal leave").defaultDays(7).isActive(true).build()
            ));
        }

        if (userRepository.count() == 0) {
            Department engineering = departmentRepository.findByName("Engineering").orElseThrow();
            Department hr = departmentRepository.findByName("Human Resources").orElseThrow();

            User admin = userRepository.save(User.builder()
                    .employeeCode("ADM001")
                    .firstName("Alex")
                    .lastName("Admin")
                    .email("admin@company.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .phone("9000000001")
                    .role(Role.ADMIN)
                    .department(hr)
                    .joiningDate(LocalDate.of(2020, 1, 1))
                    .status(EmployeeStatus.ACTIVE)
                    .build());

            User manager = userRepository.save(User.builder()
                    .employeeCode("MGR001")
                    .firstName("Morgan")
                    .lastName("Manager")
                    .email("manager@company.com")
                    .password(passwordEncoder.encode("Manager@123"))
                    .phone("9000000002")
                    .role(Role.MANAGER)
                    .department(engineering)
                    .joiningDate(LocalDate.of(2021, 3, 15))
                    .status(EmployeeStatus.ACTIVE)
                    .build());

            User employee = userRepository.save(User.builder()
                    .employeeCode("EMP001")
                    .firstName("Emma")
                    .lastName("Employee")
                    .email("employee@company.com")
                    .password(passwordEncoder.encode("Employee@123"))
                    .phone("9000000003")
                    .role(Role.EMPLOYEE)
                    .department(engineering)
                    .manager(manager)
                    .joiningDate(LocalDate.of(2022, 6, 1))
                    .status(EmployeeStatus.ACTIVE)
                    .build());

            int currentYear = Year.now().getValue();
            for (LeaveType leaveType : leaveTypeRepository.findAll()) {
                for (User user : List.of(manager, employee)) {
                    leaveBalanceRepository.save(LeaveBalance.builder()
                            .user(user)
                            .leaveType(leaveType)
                            .totalDays(leaveType.getDefaultDays().doubleValue())
                            .usedDays(0.0)
                            .remainingDays(leaveType.getDefaultDays().doubleValue())
                            .year(currentYear)
                            .build());
                }
            }
        }
    }
}
