package ru.deyev.credit.deal.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.deyev.credit.deal.model.*;
import ru.deyev.credit.deal.repository.ApplicationRepository;

import java.util.List;

@Service
@AllArgsConstructor
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
                            .passportNumber(client.getPassportNumber())
                            .passportSeries(client.getPassportSeries())
                            .passportIssueBranch(client.getPassportIssueBranch())
                            .passportIssueDate(client.getPassportIssueDate()));
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
}
