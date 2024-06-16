package com.team4.leaveprocessingsystem.controller;

import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.LeaveApplication;
import com.team4.leaveprocessingsystem.model.Manager;
import com.team4.leaveprocessingsystem.model.enums.LeaveStatusEnum;
import com.team4.leaveprocessingsystem.repository.EmployeeRepository;
import com.team4.leaveprocessingsystem.service.*;
import com.team4.leaveprocessingsystem.validator.LeaveApplicationValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("manager/leave")
@Controller
public class ManagerLeaveController {

    private final EmployeeService employeeService;
    private final LeaveApplicationValidator leaveApplicationValidator;
    private final AuthenticationService authenticationService;
    private final EmailApiService emailApiService;
    private final UserService userService;
    private final EmployeeRepository employeeRepository;
    private final LeaveApplicationService leaveApplicationService;
    private final ManagerService managerService;

    public ManagerLeaveController(EmployeeService employeeService, LeaveApplicationValidator leaveApplicationValidator, AuthenticationService authenticationService, EmailApiService emailApiService, UserService userService, EmployeeRepository employeeRepository, LeaveApplicationService leaveApplicationService, ManagerService managerService) {
        this.employeeService = employeeService;
        this.leaveApplicationValidator = leaveApplicationValidator;
        this.authenticationService = authenticationService;
        this.emailApiService = emailApiService;
        this.userService = userService;
        this.employeeRepository = employeeRepository;
        this.leaveApplicationService = leaveApplicationService;
        this.managerService = managerService;
    }

    @GetMapping("managerView")
    public String managerViewLeave(Model model, LeaveApplicationService leaveApplicationService) throws IllegalAccessException {
        Employee employee = employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        if (!authenticationService.isLoggedInAManager()) {
            throw new IllegalAccessException();
        }
        int managerId = employee.getId();
        List<LeaveApplication> subordinateLeaveApplications = leaveApplicationService.findSubordinatesLeaveApplicationsByReviewingManager_Id(managerId);
        model.addAttribute("subordinateLeaveApplications", subordinateLeaveApplications);
        return "manager/leave-application/managerViewLeave";
    }

    // MANAGER - GET - PENDING LEAVE APPLICATIONS
    @GetMapping("pendingLeaves")
    public String pendingLeaveApplications(Model model) {
        Manager currentManager = (Manager) employeeService.findEmployeeById(authenticationService.getLoggedInEmployeeId());
        Map<String, List<LeaveApplication>> pendingLeaveApplications = leaveApplicationService.findLeaveApplicationsPendingApprovalByManager(currentManager);
        model.addAttribute("pendingLeaveApplications", pendingLeaveApplications);
        return "manager/leave-application/pendingLeaveApplications";
    }

    //manager view details of his subordinate
    @GetMapping("viewSubordinateDetails/{id}")
    public String viewLeaveDetails(Model model, @PathVariable int id) {
        LeaveApplication leaveApplication = leaveApplicationService.findLeaveApplicationById(id);
        model.addAttribute("leave", leaveApplication);
        return "manager/leave-application/managerViewLeaveDetails";
    }

    // MANAGER - GET - REVIEW LEAVE APPLICATIONS DETAILS
    @GetMapping("review/{id}")
    public String leaveApplicationsDetails(@PathVariable Integer id, Model model) {
        LeaveApplication leaveApplication = leaveApplicationService.findLeaveApplicationById(id);
        model.addAttribute("leave", leaveApplication);
        return "manager/leave-application/reviewLeave";
    }

    // MANAGER - POST - REVIEW LEAVE APPLICATION
    @PostMapping("/submitLeaveApplication")
    public String reviewLeaveApplication(@Valid @ModelAttribute("leave") LeaveApplication leave,
                                         BindingResult bindingResult, Model model) {
        LeaveApplication existingLeaveApplication = leaveApplicationService.findLeaveApplicationById(leave.getId());
        existingLeaveApplication.setSubmittingEmployee(employeeService.findEmployeeById(leave.getSubmittingEmployee().getId()));
        existingLeaveApplication.setReviewingManager(managerService.findManagerById(leave.getReviewingManager().getId()));
        existingLeaveApplication.setLeaveStatus(leave.getLeaveStatus());
        existingLeaveApplication.setLeaveType(leave.getLeaveType());
        existingLeaveApplication.setStartDate(leave.getStartDate());
        existingLeaveApplication.setEndDate(leave.getEndDate());
        existingLeaveApplication.setSubmissionReason(leave.getSubmissionReason());
        existingLeaveApplication.setReviewingManager(leave.getReviewingManager());
        existingLeaveApplication.setWorkDissemination(leave.getWorkDissemination());
        existingLeaveApplication.setContactDetails(leave.getContactDetails());
        existingLeaveApplication.setRejectionReason(leave.getRejectionReason());


        // Return back to page if validation has errors
        if (bindingResult.hasErrors()) {
            return "manager/leave-application/reviewLeave";
        }

        // Check if the leave is rejected and ensure the rejection reason is provided
        if (leave.getLeaveStatus() == LeaveStatusEnum.REJECTED && (leave.getRejectionReason() == null || leave.getRejectionReason().trim().isEmpty())) {
            bindingResult.rejectValue("rejectionReason", "error.leave", "Rejection reason must be provided if the leave is rejected");
            return "manager/leave-application/reviewLeave";
        }

        // Update to Approved
        if (leave.getLeaveStatus() == LeaveStatusEnum.APPROVED){
            existingLeaveApplication.setLeaveStatus(LeaveStatusEnum.APPROVED);
            leaveApplicationService.save(existingLeaveApplication);}

        //Update to Rejected
        if (leave.getLeaveStatus() == LeaveStatusEnum.REJECTED){
            existingLeaveApplication.setLeaveStatus(LeaveStatusEnum.REJECTED);
            leaveApplicationService.save(existingLeaveApplication);}

        // Redirect to pending leave applications with a success parameter
        return "redirect:/manager/leave/pendingLeaves?updateSuccess=true";
    }
}
