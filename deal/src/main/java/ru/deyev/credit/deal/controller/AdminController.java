package ru.deyev.credit.deal.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.deyev.credit.deal.model.ApplicationDTO;
import ru.deyev.credit.deal.model.ApplicationStatus;
import ru.deyev.credit.deal.service.AdminService;

@RestController
@RequestMapping("deal/admin")
@AllArgsConstructor
public class AdminController {

    private AdminService adminService;

    @GetMapping("application/{applicationId}")
    public ApplicationDTO getApplicationById(@PathVariable Long applicationId) {
        return adminService.getApplicationById(applicationId);
    }

    @PutMapping("application/{applicationId}/status")
    public void updateApplicationStatusById(@PathVariable Long applicationId, @RequestParam String statusName) {
        adminService.updateApplicationStatusById(applicationId, ApplicationStatus.fromValue(statusName));
    }
}
