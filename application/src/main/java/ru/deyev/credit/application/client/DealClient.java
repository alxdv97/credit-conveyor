package ru.deyev.credit.application.client;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.deyev.credit.application.model.CreateLoanApplicationRequestDTO;
import ru.deyev.credit.application.model.LoanOfferDTO;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class DealClient {

    private static final String DEAL_URL = "http://localhost:8082/deal";

    private final RestTemplate restTemplate;

    public List<LoanOfferDTO> createLoanApplication(CreateLoanApplicationRequestDTO request){
        String createApplicationURI = "/application";
        LoanOfferDTO[] loanOffers = restTemplate.postForObject(DEAL_URL + createApplicationURI, request, LoanOfferDTO[].class);
        return Arrays.stream(loanOffers).collect(Collectors.toList());
    }
}
