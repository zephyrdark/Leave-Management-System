package com.team4.leaveprocessingsystem.controller.admin;

import com.team4.leaveprocessingsystem.interfacemethods.IEmployee;
import com.team4.leaveprocessingsystem.model.Employee;
import com.team4.leaveprocessingsystem.model.User;
import com.team4.leaveprocessingsystem.model.enums.RoleEnum;
import com.team4.leaveprocessingsystem.repository.UserRepository;
import com.team4.leaveprocessingsystem.service.JobDesignationService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Setter
@Controller
public class ManageStaffController {
    @Autowired
    private IEmployee employeeService;
    @Autowired
    private JobDesignationService jobDesignationService;
    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "/search-staff")
    public String search(@RequestParam(value = "keyword", required = false) String k,
                         @RequestParam(value = "searchtype", required = false) String t,
                         Model model) {

        if (k == null) model.addAttribute("staffs", employeeService.findAll());
        if (t == null) t = "";

        switch (t) {
            case (""):
                model.addAttribute("staffs", employeeService.findAll());
                break;
            case ("name"):
                model.addAttribute("staffs", employeeService.SearchEmployeeByName(k));
                break;
            case ("jobDesignation"):
                model.addAttribute("staffs", employeeService.findEmployeeByJobDesignation(k));
                break;
            case ("roleType"):
                model.addAttribute("staffs", employeeService.findUserByRoleType(k));
                break;
            default:
                return "errorStaffNotFound";
        }
        model.addAttribute("keyword", k);
        model.addAttribute("searchtype", t);
        return "searchStaffResults";
    }

    @RequestMapping(value = "edit/{id}")
    public ModelAndView showEditStaffForm(@PathVariable(name = "id") int id, Model model) {
        BiFunction<List<User>, String, Boolean> employeeHasThisUserRoleFlag = (listOfUserRoles, specificRole) -> {
            return listOfUserRoles.stream().anyMatch(user -> user.getRole().toString().equals(specificRole));
        };

        List<User> employeeUserAccountList = userRepository.findUserRolesByEmployeeId(id);

        List<String> roleEnumNamesStrings = Stream.of(RoleEnum.values())
                .map(RoleEnum::name)
                .collect(Collectors.toList());

        model.addAttribute("flag", employeeHasThisUserRoleFlag);

        ModelAndView mav = new ModelAndView("editStaff");
        Employee employee = employeeService.findEmployeeById(id);

        mav.addObject("employee", employee);
        mav.addObject("jobDesignations", jobDesignationService.listAllJobDesignations());

        mav.addObject("roles", roleEnumNamesStrings);
        mav.addObject("employeeUserAccountList", userRepository.findUserRolesByEmployeeId(id));

        return mav;
    }

}