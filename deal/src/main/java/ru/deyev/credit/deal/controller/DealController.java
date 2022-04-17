package ru.deyev.credit.deal.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.deyev.credit.deal.model.CreateLoanApplicationRequest;
import ru.deyev.credit.deal.model.Credit;
import ru.deyev.credit.deal.model.LoanOffer;
import ru.deyev.credit.deal.model.ScoringData;
import ru.deyev.credit.deal.service.DealService;

import java.util.List;

@RestController
@RequestMapping("/deal")
@AllArgsConstructor
public class DealController {

    private final DealService dealService;

    @PostMapping("/application")
    public List<LoanOffer> createApplication(@RequestBody CreateLoanApplicationRequest request) {
        return dealService.createApplication(request);
    }

    @PostMapping("/calculate")
    public Credit calculateCredit(@RequestBody ScoringData scoringData) {
        return dealService.calculateCredit(scoringData);
    }
}
