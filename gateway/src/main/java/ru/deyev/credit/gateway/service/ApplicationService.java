package ru.deyev.credit.gateway.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.deyev.credit.gateway.feign.ApplicationFeignClient;
import ru.deyev.credit.gateway.model.CreateLoanApplicationRequestDTO;
import ru.deyev.credit.gateway.model.LoanOfferDTO;

import java.util.List;

@Service
@AllArgsConstructor
public class ApplicationService {

    private final ApplicationFeignClient applicationFeignClient;

    public List<LoanOfferDTO> createLoanApplication(CreateLoanApplicationRequestDTO requestDTO){
        return applicationFeignClient.createApplication(requestDTO).getBody();
    }

}
