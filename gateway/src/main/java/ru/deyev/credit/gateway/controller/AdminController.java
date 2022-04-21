package ru.deyev.credit.gateway.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.deyev.credit.gateway.api.AdminApi;
import ru.deyev.credit.gateway.model.ApplicationDTO;
import ru.deyev.credit.gateway.model.ApplicationStatus;
import ru.deyev.credit.gateway.model.ApplicationStatusHistoryDTO;
import ru.deyev.credit.gateway.service.AdminService;

import java.util.List;

@RestController
@AllArgsConstructor
public class AdminController implements AdminApi {

    private final AdminService adminService;

    @Override
    public ResponseEntity<List<ApplicationDTO>> getAllApplications() {
        return AdminApi.super.getAllApplications();
    }

    @Override
    public ResponseEntity<ApplicationDTO> getApplicationById(String applicationId) {
        return AdminApi.super.getApplicationById(applicationId);
    }

    @Override
    public ResponseEntity<ApplicationStatus> getApplicationStatusById(String applicationId) {
        return AdminApi.super.getApplicationStatusById(applicationId);
    }

    @Override
    public ResponseEntity<List<ApplicationStatusHistoryDTO>> getApplicationStatusHistoryById(String applicationId) {
        return AdminApi.super.getApplicationStatusHistoryById(applicationId);
    }
}
