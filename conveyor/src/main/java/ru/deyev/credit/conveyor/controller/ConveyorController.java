package ru.deyev.credit.conveyor.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.deyev.credit.conveyor.model.CreateLoanApplicationRequest;
import ru.deyev.credit.conveyor.model.Credit;
import ru.deyev.credit.conveyor.model.LoanOffer;
import ru.deyev.credit.conveyor.model.ScoringData;
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
    public List<LoanOffer> generateOffers(@RequestBody CreateLoanApplicationRequest request) {
        return service.generateOffers(request);
    }

    @PostMapping("/calculation")
    public Credit calculateCredit(@RequestBody ScoringData scoringData) {
        return service.calculateCredit(scoringData);
    }
}
