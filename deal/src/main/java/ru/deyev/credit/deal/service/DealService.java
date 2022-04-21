package ru.deyev.credit.deal.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.deyev.credit.deal.feign.ConveyorFeignClient;
import ru.deyev.credit.deal.model.Application;
import ru.deyev.credit.deal.model.ApplicationStatusHistoryDTO;
import ru.deyev.credit.deal.model.Client;
import ru.deyev.credit.deal.model.CreditDTO;
import ru.deyev.credit.deal.model.LoanApplicationRequestDTO;
import ru.deyev.credit.deal.model.LoanOfferDTO;
import ru.deyev.credit.deal.model.ScoringDataDTO;
import ru.deyev.credit.deal.repository.ApplicationRepository;
import ru.deyev.credit.deal.repository.ClientRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static ru.deyev.credit.deal.model.ApplicationStatus.APPROVED;
import static ru.deyev.credit.deal.model.ApplicationStatus.PREAPPROVAL;
import static ru.deyev.credit.deal.model.ApplicationStatusHistoryDTO.ChangeTypeEnum.AUTOMATIC;

@Service
@AllArgsConstructor
@Slf4j
public class DealService {

    private final ConveyorFeignClient conveyorFeignClient;

    private final ApplicationRepository applicationRepository;

    private final ClientRepository clientRepository;

    public List<LoanOfferDTO> createApplication(@RequestBody LoanApplicationRequestDTO request) {
        Client newClient = new Client()
                .setFirstName(request.getFirstName())
                .setMiddleName(request.getMiddleName())
                .setLastName(request.getLastName())
                .setBirthDate(request.getBirthdate())
                .setPassportSeries(request.getPassportSeries())
                .setPassportNumber(request.getPassportNumber());

        Client savedClient = clientRepository.save(newClient);

        log.info("createApplication(), savedClient={}", savedClient);
        Application newApplication = new Application()
                .setClient(savedClient)
                .setCreationDate(LocalDate.now())
                .setStatus(PREAPPROVAL)
                .setStatusHistory(List.of(
                        new ApplicationStatusHistoryDTO()
                                .status(PREAPPROVAL)
                                .time(LocalDateTime.now())
                                .changeType(AUTOMATIC)));

        Application savedApplication = applicationRepository.save(newApplication);
        clientRepository.save(savedClient.setApplication(savedApplication));

        List<LoanOfferDTO> loanOffers = conveyorFeignClient.generateOffers(request).getBody();

        assert loanOffers != null;
        loanOffers.forEach(loanOfferDTO -> loanOfferDTO.setApplicationId(savedApplication.getId()));


        log.info("createApplication(), savedApplication={}", savedApplication);
        log.info("Received offers: {}", loanOffers);
        return loanOffers;
    }

    public CreditDTO calculateCredit(ScoringDataDTO scoringData) {
//        TODO update application in db
        return conveyorFeignClient.calculateCredit(scoringData).getBody();
    }

    public void applyOffer(LoanOfferDTO loanOfferDTO) {
        Application application = applicationRepository.getById(loanOfferDTO.getApplicationId());
        List<ApplicationStatusHistoryDTO> statusHistory = application.getStatusHistory();

        statusHistory.add(
                new ApplicationStatusHistoryDTO()
                        .status(APPROVED)
                        .time(LocalDateTime.now())
                        .changeType(AUTOMATIC));

        Application updatedApplication = applicationRepository.save(
                application
                        .setStatus(APPROVED)
                        .setAppliedOffer(loanOfferDTO)
                        .setStatusHistory(statusHistory)
        );
        log.info("applyOffer - end, updatedApplication={}", updatedApplication);
    }
}
