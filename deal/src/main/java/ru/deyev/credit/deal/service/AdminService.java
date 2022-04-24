package ru.deyev.credit.deal.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.deyev.credit.deal.model.*;
import ru.deyev.credit.deal.repository.ApplicationRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class AdminService {

    private ApplicationRepository applicationRepository;

    public ApplicationDTO getApplicationById(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application with id = " + applicationId + " not found"));

        return toDTO(application);
    }

    public ApplicationDTO toDTO(Application application) {
        Client client = application.getClient();
        Credit credit = application.getCredit();

        ApplicationDTO applicationDTO = new ApplicationDTO();

        if (client != null) {
            applicationDTO
                    .client(new ClientDTO()
                            .account(client.getAccount())
                            .firstName(client.getFirstName())
                            .lastName(client.getLastName())
                            .middleName(client.getMiddleName())
                            .birthdate(client.getBirthDate())
                            .gender(client.getGender())
                            .email(client.getEmail())
                            .dependentAmount(client.getDependentAmount())
                            .maritalStatus(client.getMaritalStatus())
                            .employment(client.getEmploymentDTO())
                            .passportNumber(client.getPassportInfo().getNumber())
                            .passportSeries(client.getPassportInfo().getSeries())
                            .passportIssueBranch(client.getPassportInfo().getIssueBranch())
                            .passportIssueDate(client.getPassportInfo().getIssueDate()));
        }

        if (credit != null) {
            applicationDTO
                    .credit(new CreditDTO()
                            .id(credit.getId())
                            .amount(credit.getAmount())
                            .paymentSchedule(credit.getPaymentSchedule())
                            .monthlyPayment(credit.getMonthlyPayment())
                            .psk(credit.getPsk())
                            .term(credit.getTerm())
                            .rate(credit.getRate())
                            .isInsuranceEnabled(credit.getIsInsuranceEnabled())
                            .isSalaryClient(credit.getIsSalaryClient()));
        }

        if (application.getSignDate() != null) {
            applicationDTO.signDate(application.getSignDate().atStartOfDay());
        }

        if (application.getSesCode() != null) {
            applicationDTO.sesCode(application.getSesCode().toString());
        }

        List<ApplicationStatusHistoryDTO> statusHistory = application.getStatusHistory();

        return applicationDTO
                .id(application.getId())
                .statusHistory(statusHistory)
                .status(application.getStatus())
                .creationDate(application.getCreationDate().atStartOfDay());
    }

    public void updateApplicationStatusById(Long applicationId, ApplicationStatus status) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application with id " + applicationId + " not found."));

        log.info("Updating application {} status from {} to {}",
                applicationId, application.getStatus(), status);

        List<ApplicationStatusHistoryDTO> statusHistory = application.getStatusHistory();
        statusHistory.add(new ApplicationStatusHistoryDTO()
                .status(status)
                .time(LocalDateTime.now())
                .changeType(ApplicationStatusHistoryDTO.ChangeTypeEnum.AUTOMATIC));

        applicationRepository.save(application
                .setStatus(status)
                .setStatusHistory(statusHistory));
    }

    public List<ApplicationDTO> getAllApplications() {
        return applicationRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
