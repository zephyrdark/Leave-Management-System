package com.team4.leaveprocessingsystem.interfacemethods;

import com.team4.leaveprocessingsystem.model.CompensationClaim;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.Manager;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;

public interface ICompensationClaim {
    boolean save(CompensationClaim compensationClaim);

    List<CompensationClaim> findCompensationClaimsByEmployee(Employee employee);

    long count();

    CompensationClaim findCompensationClaimById(Integer id);

    CompensationClaim findCompensationClaimIfBelongsToEmployee(Integer id, Employee employee);

    float calculateOvertimeHours(CompensationClaim compensationClaim);

    float calculateLeaveRequested(CompensationClaim compensationClaim);

    Map<String, List<CompensationClaim>> findCompensationClaimsPendingReviewByManager(Manager manager);

    // find all approving_manager_ids
    @Transactional
    List<Integer> allApprovingManagersIds();

    // find all claiming_employee_ids
    @Transactional
    List<Integer> allClaimingEmployees();
}