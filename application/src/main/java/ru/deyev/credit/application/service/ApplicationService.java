package ru.deyev.credit.application.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.deyev.credit.application.client.DealClient;
import ru.deyev.credit.application.model.CreateLoanApplicationRequestDTO;
import ru.deyev.credit.application.model.LoanOfferDTO;

import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ApplicationService {

    private final DealClient dealClient;

    public List<LoanOfferDTO> createLoanApplication(CreateLoanApplicationRequestDTO request) {
        List<LoanOfferDTO> loanOffers = dealClient.createLoanApplication(request);
        log.info("Received offers: {}", Arrays.deepToString(loanOffers.toArray()));
        return loanOffers;
    }
}
