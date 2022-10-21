package ru.deyev.credit.deal.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.deyev.credit.deal.model.ApplicationDTO;
import ru.deyev.credit.deal.model.ApplicationStatus;
import ru.deyev.credit.deal.service.AdminService;

import java.util.List;

@RestController
@RequestMapping("/deal/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/application/{applicationId}")
    public ApplicationDTO getApplicationById(@PathVariable Long applicationId) {
        return adminService.getApplicationById(applicationId);
    }

    @GetMapping("/application/all")
    public List<ApplicationDTO> getAllApplications() {
        return adminService.getAllApplications();
    }

    @PutMapping("/application/{applicationId}/status")
    public void updateApplicationStatusById(@PathVariable Long applicationId, @RequestParam String statusName) {
        adminService.updateApplicationStatusById(applicationId, ApplicationStatus.fromValue(statusName));
    }
}
