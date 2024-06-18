package com.team4.leaveprocessingsystem.model.dataTransferObjects;

import com.team4.leaveprocessingsystem.model.CompensationClaim;
import com.team4.leaveprocessingsystem.model.Employee;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class ReportingDTO {
    private ArrayList<Employee> employees;
    private ArrayList<CompensationClaim> claims;

    public ReportingDTO(ArrayList<Employee> employees, ArrayList<CompensationClaim> claims) {
        this.employees = employees;
        this.claims = claims;
    }

}