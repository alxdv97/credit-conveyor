package ru.deyev.credit.conveyor.service;

import org.springframework.stereotype.Service;
import ru.deyev.credit.conveyor.model.CreateLoanApplicationRequest;
import ru.deyev.credit.conveyor.model.LoanOffer;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OfferService {

    private final ScoringService scoringService;

    public OfferService(ScoringService scoringService) {
        this.scoringService = scoringService;
    }

    public List<LoanOffer> generateOffers(CreateLoanApplicationRequest request) {
        return List.of(
                createOffer(false, false, request),
                createOffer(true, false, request),
                createOffer(false, true, request),
                createOffer(true, true, request)
        );
    }

    private LoanOffer createOffer(Boolean isInsuranceEnabled,
                                  Boolean isSalaryClient,
                                  CreateLoanApplicationRequest request) {

        BigDecimal totalAmount = scoringService.evaluateTotalAmountByServices(request.getAmount(),
                isInsuranceEnabled);

        BigDecimal rate = scoringService.calculateRate(isInsuranceEnabled, isSalaryClient);

        return new LoanOffer()
                .requestedAmount(request.getAmount())
                .totalAmount(totalAmount)
                .term(request.getTerm())
                .isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient)
                .rate(rate)
                .monthlyPayment(scoringService.calculateMonthlyPayment(totalAmount, request.getTerm(), rate));
    }
}
