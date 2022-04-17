package ru.deyev.credit.gateway.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.deyev.credit.gateway.client.ApplicationClient;
import ru.deyev.credit.gateway.model.CreateLoanApplicationRequestDTO;
import ru.deyev.credit.gateway.model.LoanOfferDTO;

import java.util.List;

@Service
@AllArgsConstructor
public class ApplicationService {

    private final ApplicationClient applicationClient;

    public List<LoanOfferDTO> createLoanApplication(CreateLoanApplicationRequestDTO requestDTO){
        return applicationClient.createLoanApplication(requestDTO);
    }

}
