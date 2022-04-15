package ru.deyev.credit.gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import ru.deyev.credit.gateway.api.ApplicationApi;
import ru.deyev.credit.gateway.model.CreateLoanApplicationRequestDTO;
import ru.deyev.credit.gateway.model.LoanOfferDTO;

import java.util.List;
import java.util.Optional;
@RestController
public class ApplicationApiImpl implements ApplicationApi {
    @Override
    public Optional<NativeWebRequest> getRequest() {
        return ApplicationApi.super.getRequest();
    }

    @Override
    public ResponseEntity<List<LoanOfferDTO>> _createLoanApplication(CreateLoanApplicationRequestDTO createLoanApplicationRequestDTO) {
        return ApplicationApi.super._createLoanApplication(createLoanApplicationRequestDTO);
    }

    @Override
    public ResponseEntity<List<LoanOfferDTO>> createLoanApplication(CreateLoanApplicationRequestDTO createLoanApplicationRequestDTO) {
        return ApplicationApi.super.createLoanApplication(createLoanApplicationRequestDTO);
    }
}
