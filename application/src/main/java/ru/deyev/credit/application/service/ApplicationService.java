package ru.deyev.credit.application.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.deyev.credit.application.feign.DealFeignClient;
import ru.deyev.credit.application.model.CreateLoanApplicationRequestDTO;
import ru.deyev.credit.application.model.LoanOfferDTO;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ApplicationService {

    private final DealFeignClient dealFeignClient;

    public List<LoanOfferDTO> createLoanApplication(CreateLoanApplicationRequestDTO request) {
        List<LoanOfferDTO> loanOffers = dealFeignClient.createApplication(request).getBody();
        log.info("Received offers: {}", loanOffers);
        return loanOffers;
    }
}
