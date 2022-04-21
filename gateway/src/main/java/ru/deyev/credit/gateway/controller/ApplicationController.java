package ru.deyev.credit.gateway.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.deyev.credit.gateway.api.ApplicationApi;
import ru.deyev.credit.gateway.model.FinishRegistrationRequestDTO;
import ru.deyev.credit.gateway.model.LoanApplicationRequestDTO;
import ru.deyev.credit.gateway.model.LoanOfferDTO;
import ru.deyev.credit.gateway.service.ApplicationService;

import java.util.List;

@RestController
@AllArgsConstructor
public class ApplicationController implements ApplicationApi {

    private final ApplicationService applicationService;

    @Override
    public ResponseEntity<Void> applyOffer(LoanOfferDTO loanOfferDTO) {
        applicationService.applyOffer(loanOfferDTO);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<LoanOfferDTO>> createLoanApplication(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        return ResponseEntity.ok(applicationService.createLoanApplication(loanApplicationRequestDTO));
    }

    @Override
    public ResponseEntity<Void> denyLoanApplication() {
        return ApplicationApi.super.denyLoanApplication();
    }

    @Override
    public ResponseEntity<Void> finishRegistration(FinishRegistrationRequestDTO finishRegistrationRequestDTO) {
        return ApplicationApi.super.finishRegistration(finishRegistrationRequestDTO);
    }
}
