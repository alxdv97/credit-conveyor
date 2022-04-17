package ru.deyev.credit.gateway.client;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.deyev.credit.gateway.model.CreateLoanApplicationRequestDTO;
import ru.deyev.credit.gateway.model.LoanOfferDTO;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ApplicationClient {

    private static final String APPLICATION_URL = "http://localhost:8081/application";

    private final RestTemplate restTemplate;

    public List<LoanOfferDTO> createLoanApplication(CreateLoanApplicationRequestDTO request){
        LoanOfferDTO[] loanOffers = restTemplate.postForObject(APPLICATION_URL, request, LoanOfferDTO[].class);
        return Arrays.stream(loanOffers).collect(Collectors.toList());
    }



}
