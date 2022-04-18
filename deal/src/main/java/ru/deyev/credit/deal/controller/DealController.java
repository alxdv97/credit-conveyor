package ru.deyev.credit.deal.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.deyev.credit.deal.model.CreateLoanApplicationRequestDTO;
import ru.deyev.credit.deal.model.Credit;
import ru.deyev.credit.deal.model.CreditDTO;
import ru.deyev.credit.deal.model.LoanOfferDTO;
import ru.deyev.credit.deal.model.ScoringDataDTO;
import ru.deyev.credit.deal.service.DealService;

import java.util.List;

@RestController
@RequestMapping("/deal")
@AllArgsConstructor
public class DealController {

    private final DealService dealService;

    @PostMapping("/application")
    public ResponseEntity<List<LoanOfferDTO>> createApplication(@RequestBody CreateLoanApplicationRequestDTO request) {
        return ResponseEntity.ok(dealService.createApplication(request));
    }

    @PostMapping("/calculate")
    public CreditDTO calculateCredit(@RequestBody ScoringDataDTO scoringData) {
        return dealService.calculateCredit(scoringData);
    }
}
