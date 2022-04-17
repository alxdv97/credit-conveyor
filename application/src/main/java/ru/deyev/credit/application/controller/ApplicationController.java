package ru.deyev.credit.application.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.deyev.credit.application.model.CreateLoanApplicationRequestDTO;
import ru.deyev.credit.application.model.LoanOfferDTO;
import ru.deyev.credit.application.service.ApplicationService;

import java.util.List;

@RestController
@RequestMapping("/application")
@AllArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    public List<LoanOfferDTO> createApplication(@RequestBody CreateLoanApplicationRequestDTO request) {
        return applicationService.createLoanApplication(request);
    }
}
