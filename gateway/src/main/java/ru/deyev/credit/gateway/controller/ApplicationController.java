package ru.deyev.credit.gateway.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.deyev.credit.gateway.api.ApplicationApi;
import ru.deyev.credit.gateway.model.CreateLoanApplicationRequestDTO;
import ru.deyev.credit.gateway.model.FinishRegistrationRequestDTO;
import ru.deyev.credit.gateway.model.LoanOfferDTO;
import ru.deyev.credit.gateway.service.ApplicationService;

import java.util.List;

@RestController
@AllArgsConstructor
public class ApplicationController implements ApplicationApi {

    private final ApplicationService applicationService;

    @Override
    public ResponseEntity<Void> applyOffer(Integer offerId) {
        return ApplicationApi.super.applyOffer(offerId);
    }

    @Override
    public ResponseEntity<List<LoanOfferDTO>> createLoanApplication(CreateLoanApplicationRequestDTO createLoanApplicationRequestDTO) {
        return ResponseEntity.ok(applicationService.createLoanApplication(createLoanApplicationRequestDTO));
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
