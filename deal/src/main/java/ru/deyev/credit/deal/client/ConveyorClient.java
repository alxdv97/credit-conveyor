package ru.deyev.credit.deal.client;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.deyev.credit.deal.model.CreateLoanApplicationRequest;
import ru.deyev.credit.deal.model.Credit;
import ru.deyev.credit.deal.model.LoanOffer;
import ru.deyev.credit.deal.model.ScoringData;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ConveyorClient {

    private final static String CONVEYOR_URL = "http://localhost:8080/conveyor";
    private final RestTemplate restTemplate;

    public List<LoanOffer> generateOffers(CreateLoanApplicationRequest request) {
        String generateOffersURI = "/offers";
        LoanOffer[] loanOffers = restTemplate.postForObject(CONVEYOR_URL + generateOffersURI, request, LoanOffer[].class);
        return Arrays.stream(loanOffers).collect(Collectors.toList());
    }

    public Credit calculateCredit(ScoringData scoringData) {
        String calculateCreditURI = "/calculation";
        return restTemplate.postForObject(CONVEYOR_URL + calculateCreditURI, scoringData, Credit.class);
    }
}
