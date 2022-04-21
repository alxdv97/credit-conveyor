package ru.deyev.credit.deal.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.deyev.credit.deal.model.CreditDTO;
import ru.deyev.credit.deal.model.LoanApplicationRequestDTO;
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
    public ResponseEntity<List<LoanOfferDTO>> createApplication(@RequestBody LoanApplicationRequestDTO request) {
        return ResponseEntity.ok(dealService.createApplication(request));
    }

    @PutMapping("/offer")
    public ResponseEntity<Void> applyOffer(@RequestBody LoanOfferDTO request) {
        dealService.applyOffer(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/calculate")
    public CreditDTO calculateCredit(@RequestBody ScoringDataDTO scoringData) {
        return dealService.calculateCredit(scoringData);
    }
}
