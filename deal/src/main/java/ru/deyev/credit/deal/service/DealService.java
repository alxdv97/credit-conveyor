package ru.deyev.credit.deal.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.deyev.credit.deal.client.ConveyorClient;
import ru.deyev.credit.deal.model.CreateLoanApplicationRequest;
import ru.deyev.credit.deal.model.Credit;
import ru.deyev.credit.deal.model.LoanOffer;
import ru.deyev.credit.deal.model.ScoringData;

import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class DealService {

    private final ConveyorClient conveyorClient;

    public List<LoanOffer> createApplication(@RequestBody CreateLoanApplicationRequest request) {
//        TODO save application to db
        List<LoanOffer> loanOffers = conveyorClient.generateOffers(request);
        log.info("Received offers: {}", Arrays.deepToString(loanOffers.toArray()));
        return loanOffers;
    }

    public Credit calculateCredit(ScoringData scoringData) {
//        TODO update application in db
        return conveyorClient.calculateCredit(scoringData);
    }
}
