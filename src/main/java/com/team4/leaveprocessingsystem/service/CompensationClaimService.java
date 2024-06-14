package com.team4.leaveprocessingsystem.service;

import com.team4.leaveprocessingsystem.exception.CompensationClaimNotFoundException;
import com.team4.leaveprocessingsystem.exception.ServiceSaveException;
import com.team4.leaveprocessingsystem.interfacemethods.ICompensationClaim;
import com.team4.leaveprocessingsystem.model.CompensationClaim;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.Manager;
import com.team4.leaveprocessingsystem.model.enums.CompensationClaimStatusEnum;
import com.team4.leaveprocessingsystem.repository.CompensationClaimRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CompensationClaimService implements ICompensationClaim {

    private final CompensationClaimRepository compensationClaimRepository;
    private final EmployeeService employeeService;

    @Autowired
    public CompensationClaimService(CompensationClaimRepository compensationClaimRepository, EmployeeService employeeService) {
        this.compensationClaimRepository = compensationClaimRepository;
        this.employeeService = employeeService;
    }

    @Override
    @Transactional
    public boolean save(CompensationClaim compensationClaim) {
        try {
            compensationClaimRepository.save(compensationClaim);
            return true;
        } catch (Exception e) {
            throw new ServiceSaveException("compensation claim from: " +
                    compensationClaim.getClaimingEmployee().getName(), e);
        }
    }

    @Override
    @Transactional
    public boolean isClashWithExistingCompensationClaims(CompensationClaim targetClaim) {
        List<CompensationClaim> appliedUpdatedApprovedClaims =
                compensationClaimRepository.findAppliedUpdatedApprovedByClaimingEmployeeId
                        (targetClaim.getClaimingEmployee().getId());
        // Remove target claim from list if it is being edited, so it will not be invalidated
        if (targetClaim.getId() != null) {
            appliedUpdatedApprovedClaims.remove(targetClaim);
        }
        // Loop through each existingClaim and check for timing clashes
        for (CompensationClaim existingClaim : appliedUpdatedApprovedClaims) {
            if (existingClaim.getOvertimeStartDateTime().compareTo(targetClaim.getOvertimeEndDateTime())
                    <= 0 && existingClaim.getOvertimeEndDateTime().compareTo(existingClaim.getOvertimeStartDateTime()) >= 0){
                return true;
            };
        }
        return false;
    }

    @Override
    @Transactional
    public float calculateOvertimeHours(CompensationClaim compensationClaim) {
        LocalDateTime start = compensationClaim.getOvertimeStartDateTime();
        LocalDateTime end = compensationClaim.getOvertimeEndDateTime();
        if (start == null || end == null) return 0;
        return (start.isBefore(end)) ? start.until(end, ChronoUnit.HOURS) : 0;
    }

    @Override
    @Transactional
    public float calculateLeaveRequested(CompensationClaim compensationClaim) {
        return (int) (calculateOvertimeHours(compensationClaim) / 4) * 0.5f;
    }

    public long count() {
        return compensationClaimRepository.count();
    }


    @Override
    @Transactional
    public CompensationClaim findCompensationClaimById(Integer id) {
        return compensationClaimRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public CompensationClaim findCompensationClaimIfBelongsToEmployee(Integer id, Employee employee) {
        CompensationClaim compensationClaim = findCompensationClaimById(id);
        // Ensure that the CompensationClaim is only accessed by its ClaimingEmployee.
        if (!compensationClaim.getClaimingEmployee().getId().equals(employee.getId())) {
            throw new CompensationClaimNotFoundException("Claim does not belong to "+employee.getName()+".");
        }
        return compensationClaim;
    }

    @Override
    @Transactional
    public CompensationClaim findCompensationClaimIfBelongsToManagerForReview(Integer id, Manager manager) {
        CompensationClaim compensationClaim = findCompensationClaimById(id);
        // Ensure that the CompensationClaim is only accessed by its ApprovingManager.
        if (!compensationClaim.getApprovingManager().getId().equals(manager.getId())) {
            throw new CompensationClaimNotFoundException("Claim is not assigned to "+manager.getName()+" for review.");
        }
        return compensationClaim;
    }

    public List<CompensationClaim> findCompensationClaimsByEmployee(Employee employee) {
        try {
            return compensationClaimRepository.findByClaimingEmployee(employee);
        } catch (CompensationClaimNotFoundException e) {
            throw new CompensationClaimNotFoundException(employee.getName(), e);
        }
    }

    @Override
    @Transactional
    public Map<String, List<CompensationClaim>> findCompensationClaimsPendingReviewByManager(Manager manager) {
        Map<String, List<CompensationClaim>> compensationClaims = new HashMap<>();
        List<Employee> employeeList = employeeService.findEmployeesByManager(manager);
        for (Employee employee : employeeList) {
            List<CompensationClaim> claims = findCompensationClaimsByEmployee(employee)
                    .stream()
                    .filter(claim -> claim.getCompensationClaimStatus() == CompensationClaimStatusEnum.APPLIED
                        || claim.getCompensationClaimStatus() == CompensationClaimStatusEnum.UPDATED)
                    .toList();
            if (!claims.isEmpty()) {
                compensationClaims.put(employee.getName(), claims);
            }
        }
        return compensationClaims;
    }

}
