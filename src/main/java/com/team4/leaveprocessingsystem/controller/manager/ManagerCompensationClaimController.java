package com.team4.leaveprocessingsystem.controller.manager;

import com.team4.leaveprocessingsystem.model.CompensationClaim;
import com.team4.leaveprocessingsystem.model.Manager;
import com.team4.leaveprocessingsystem.model.enums.CompensationClaimStatusEnum;
import com.team4.leaveprocessingsystem.service.auth.AuthenticationService;
import com.team4.leaveprocessingsystem.service.repo.CompensationClaimService;
import com.team4.leaveprocessingsystem.service.repo.EmployeeService;
import com.team4.leaveprocessingsystem.service.repo.LeaveBalanceService;
import com.team4.leaveprocessingsystem.service.repo.ManagerService;
import com.team4.leaveprocessingsystem.service.reporting.DataExportService;
import com.team4.leaveprocessingsystem.util.StringCleaningUtil;
import com.team4.leaveprocessingsystem.validator.CompensationClaimValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RequestMapping("/manager/compensation-claims")
@Controller
public class ManagerCompensationClaimController {

    private final LeaveBalanceService leaveBalanceService;
    private final CompensationClaimService compensationClaimService;
    private final AuthenticationService authenticationService;
    private final CompensationClaimValidator compensationClaimValidator;
    private final EmployeeService employeeService;
    private final ManagerService managerService;

    @InitBinder
    private void initCompensationClaimBinder(WebDataBinder binder) {
        binder.addValidators(compensationClaimValidator);
    }

    @Autowired
    public ManagerCompensationClaimController(AuthenticationService authenticationService,
                                              EmployeeService employeeService,
                                              ManagerService managerService,
                                              DataExportService dataExportService,
                                              LeaveBalanceService leaveBalanceService,
                                              CompensationClaimService compensationClaimService,
                                              CompensationClaimValidator validator) {

        this.authenticationService = authenticationService;
        this.employeeService = employeeService;
        this.managerService = managerService;
        this.leaveBalanceService = leaveBalanceService;
        this.compensationClaimService = compensationClaimService;
        this.compensationClaimValidator = validator;
    }
    /*
        MANAGER - GET - PENDING COMPENSATION CLAIMS
    */
    @GetMapping("viewPendingApproval")
    public String viewPendingApproval(Model model) {
        Manager manager = managerService.findManagerById(authenticationService.getLoggedInEmployeeId());
        Map<String, List<CompensationClaim>> employeesPendingClaims = compensationClaimService.findPendingReviewByManager(manager);
        model.addAttribute("employeesPendingClaims", employeesPendingClaims);
        return "manager/compensation-claims/view-pending-approval";
    }

    /*
        MANAGER - GET - REVIEW COMPENSATION CLAIM
    */
    @GetMapping("approvalForm/{id}")
    public String approvalForm(@PathVariable Integer id, Model model) {
        Manager manager = managerService.findManagerById(authenticationService.getLoggedInEmployeeId());
        CompensationClaim claim = compensationClaimService.findIfBelongsToManagerForReview(id, manager);
        model.addAttribute("compensationClaim", claim);
        return "manager/compensation-claims/approval-form";
    }

    /*
        MANAGER - POST - REVIEW COMPENSATION CLAIM
    */
    @PostMapping("approvalForm")
    public String approvalForm(@Valid @ModelAttribute("compensationClaim") CompensationClaim claim,
                                          BindingResult result,
                                          Model model) {
        if (result.hasErrors()) {   // Return back to page if validation has errors
            model.addAttribute("compensationClaim", claim);
            return "manager/compensation-claims/approval-form";
        }
        if (claim.getClaimStatus() == CompensationClaimStatusEnum.APPROVED) {
            leaveBalanceService.updateCompensationLeave(claim);
        } else {
            claim.setClaimStatus(CompensationClaimStatusEnum.REJECTED);
        }
        claim.setComments(StringCleaningUtil.forDatabase(claim.getComments()));
        claim.setReviewedDateTime(LocalDateTime.now());
        compensationClaimService.save(claim);
        return "redirect:/manager/compensation-claims/viewPendingApproval";
    }
}
