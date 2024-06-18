package com.team4.leaveprocessingsystem.service;

import com.team4.leaveprocessingsystem.interfacemethods.IDataExport;
import com.team4.leaveprocessingsystem.model.CompensationClaim;
import com.team4.leaveprocessingsystem.util.StringCleaningUtil;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataExportService implements IDataExport {

    @Override
    public void downloadManagerReportingCompensationClaimsCSV(PrintWriter printWriter, List<CompensationClaim> list) {
        printWriter.write("Id, Claim Status, Claiming Employee, OvertimeStart, OvertimeEnd, OvertimeHours, " +
                "CompensationLeaveRequested, ClaimDateTime, ApprovingManager, ReviewedDateTime, Comments \n");
        for(CompensationClaim claim: list) {
            printWriter.write(claim.getId()
                    + "," + claim.getClaimStatus().name()
                    + "," + claim.getClaimingEmployee().getName()
                    + "," + claim.getOvertimeStart().format(DateTimeFormatter.ofPattern("dd/MMM/yyyy HH:mm"))
                    + "," + claim.getOvertimeEnd().format(DateTimeFormatter.ofPattern("dd/MMM/yyyy HH:mm"))
                    + "," + claim.getOvertimeHours()
                    + "," + claim.getCompensationLeaveRequested()
                    + "," + claim.getClaimDateTime().format(DateTimeFormatter.ofPattern("dd/MMM/yyyy HH:mm"))
                    + "," + claim.getApprovingManager().getName()
                    + "," + (((claim.getReviewedDateTime() != null) ? // date != null ? date : null
                    claim.getReviewedDateTime().format(DateTimeFormatter.ofPattern("dd/MMM/yyyy HH:mm")) : null))
                    + "," + StringCleaningUtil.forCSV(claim.getComments()) + "\n"
            );
        }
        printWriter.close();
    }
}