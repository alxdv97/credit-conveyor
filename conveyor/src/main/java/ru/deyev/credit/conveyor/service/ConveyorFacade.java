package ru.deyev.credit.conveyor.service;

import org.springframework.stereotype.Service;
import ru.deyev.credit.conveyor.model.CreateLoanApplicationRequest;
import ru.deyev.credit.conveyor.model.Credit;
import ru.deyev.credit.conveyor.model.LoanOffer;
import ru.deyev.credit.conveyor.model.ScoringData;

import java.util.List;

@Service
public class ConveyorFacade {

    private final OfferService offerService;
    private final ScoringService scoringService;

    public ConveyorFacade(OfferService offerService, ScoringService scoringService) {
        this.offerService = offerService;
        this.scoringService = scoringService;
    }

    public List<LoanOffer> generateOffers(CreateLoanApplicationRequest request) {
        return offerService.generateOffers(request);
    }

    public Credit calculateCredit(ScoringData scoringData) {
        return scoringService.calculateCredit(scoringData);
    }

}
