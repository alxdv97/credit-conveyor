package ru.deyev.credit.deal.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.deyev.credit.deal.feign.ConveyorFeignClient;
import ru.deyev.credit.deal.model.CreateLoanApplicationRequestDTO;
import ru.deyev.credit.deal.model.CreditDTO;
import ru.deyev.credit.deal.model.LoanOfferDTO;
import ru.deyev.credit.deal.model.ScoringDataDTO;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class DealService {

    private final ConveyorFeignClient conveyorFeignClient;

    public List<LoanOfferDTO> createApplication(@RequestBody CreateLoanApplicationRequestDTO request) {
//        TODO save application to db
        List<LoanOfferDTO> loanOffers = conveyorFeignClient.generateOffers(request).getBody();
        log.info("Received offers: {}", loanOffers);
        return loanOffers;
    }

    public CreditDTO calculateCredit(ScoringDataDTO scoringData) {
//        TODO update application in db
        return conveyorFeignClient.calculateCredit(scoringData).getBody();
    }
}
