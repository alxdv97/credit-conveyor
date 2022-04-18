package ru.deyev.credit.conveyor.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.deyev.credit.conveyor.model.CreateLoanApplicationRequestDTO;
import ru.deyev.credit.conveyor.model.CreditDTO;
import ru.deyev.credit.conveyor.model.LoanOfferDTO;
import ru.deyev.credit.conveyor.model.ScoringDataDTO;
import ru.deyev.credit.conveyor.service.ConveyorFacade;

import java.util.List;

@RestController
@RequestMapping("conveyor")
public class ConveyorController {

    private final ConveyorFacade service;

    public ConveyorController(ConveyorFacade service) {
        this.service = service;
    }

    @PostMapping("/offers")
    public ResponseEntity<List<LoanOfferDTO>> generateOffers(@RequestBody CreateLoanApplicationRequestDTO request) {
        return ResponseEntity.ok(service.generateOffers(request));
    }

    @PostMapping("/calculation")
    public ResponseEntity<CreditDTO> calculateCredit(@RequestBody ScoringDataDTO scoringData) {
        return ResponseEntity.ok(service.calculateCredit(scoringData));
    }
}
