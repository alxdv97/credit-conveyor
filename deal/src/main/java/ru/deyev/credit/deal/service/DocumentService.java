package ru.deyev.credit.deal.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.deyev.credit.deal.exception.DealException;
import ru.deyev.credit.deal.model.*;
import ru.deyev.credit.deal.repository.ApplicationRepository;
import ru.deyev.credit.deal.repository.CreditRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

import static ru.deyev.credit.deal.model.ApplicationStatus.PREPARE_DOCUMENTS;
import static ru.deyev.credit.deal.model.ApplicationStatusHistoryDTO.ChangeTypeEnum.MANUAL;

@Service
@AllArgsConstructor
@Slf4j
public class DocumentService {

    private DossierService dossierService;
    private ApplicationRepository applicationRepository;
    private CreditRepository creditRepository;

    public void createDocuments(Long applicationId) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application with id " + applicationId + " not found."));

        if (application.getStatus() != ApplicationStatus.CC_APPROVED) {
            throw new DealException("Application " + applicationId + " in status " + application.getStatus()
                    + ", but should be in status " + ApplicationStatus.CC_APPROVED);
        }

        log.info("Sending create document request for application {}, to email {}",
                application, application.getClient().getEmail());

        dossierService.sendMessage(new EmailMessage()
                .theme(EmailMessage.ThemeEnum.CREATE_DOCUMENT)
                .applicationId(applicationId)
                .address(application.getClient().getEmail()));
    }

    public void sendDocuments(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application with id " + applicationId + " not found."));

        if (application.getStatus() != PREPARE_DOCUMENTS) {
            throw new DealException("Application " + applicationId + " in status " + application.getStatus()
                    + ", but should be in status " + PREPARE_DOCUMENTS);
        }

        List<ApplicationStatusHistoryDTO> statusHistory = application.getStatusHistory();
        statusHistory.add(
                new ApplicationStatusHistoryDTO()
                        .status(PREPARE_DOCUMENTS)
                        .time(LocalDateTime.now())
                        .changeType(MANUAL));

        applicationRepository.save(application
                .setStatus(PREPARE_DOCUMENTS)
                .setStatusHistory(statusHistory));

        log.info("Sending send document request for application {}, to email {}",
                application, application.getClient().getEmail());

        dossierService.sendMessage(new EmailMessage()
                .theme(EmailMessage.ThemeEnum.SEND_DOCUMENT)
                .applicationId(applicationId)
                .address(application.getClient().getEmail()));
    }

    public void signDocuments(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application with id " + applicationId + " not found."));

        if (application.getStatus() != ApplicationStatus.DOCUMENT_CREATED) {
            throw new DealException("Application " + applicationId + " in status " + application.getStatus()
                    + ", but should be in status " + ApplicationStatus.DOCUMENT_CREATED);
        }

        Integer sesCode = generateSesCode();

        applicationRepository.save(application.setSesCode(sesCode));

        log.info("Sending send sign document request for application {}, to email {}",
                application, application.getClient().getEmail());

        dossierService.sendMessage(new EmailMessage()
                .theme(EmailMessage.ThemeEnum.SEND_SES)
                .applicationId(applicationId)
                .address(application.getClient().getEmail()));


    }

    public void verifyCode(Long applicationId, Integer sesCode) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application with id " + applicationId + " not found."));

        if (application.getStatus() != ApplicationStatus.DOCUMENT_CREATED) {
            throw new DealException("Application " + applicationId + " in status " + application.getStatus()
                    + ", but should be in status " + ApplicationStatus.DOCUMENT_CREATED);
        }

        if (!sesCode.equals(application.getSesCode())) {
            throw new DealException("For application " + applicationId + " wrong SES code " + sesCode
                    + ". It should be in status " + application.getSesCode());
        }

        List<ApplicationStatusHistoryDTO> statusHistory = application.getStatusHistory();
        statusHistory.add(new ApplicationStatusHistoryDTO()
                .status(ApplicationStatus.DOCUMENT_SIGNED)
                .time(LocalDateTime.now())
                .changeType(ApplicationStatusHistoryDTO.ChangeTypeEnum.MANUAL));


        applicationRepository.save(application.setStatus(ApplicationStatus.DOCUMENT_SIGNED)
                .setStatusHistory(statusHistory));

        issueCredit(applicationId);
    }

    private void issueCredit(Long applicationId) {
//        imitate long credit issuing action
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Application with id " + applicationId + " not found."));

        Long creditId = application.getCredit().getId();
        Credit credit = creditRepository.findById(creditId)
                .orElseThrow(() -> new EntityNotFoundException("Credit with id " + creditId + " not found."));

        List<ApplicationStatusHistoryDTO> statusHistory = application.getStatusHistory();
        statusHistory.add(new ApplicationStatusHistoryDTO()
                .status(ApplicationStatus.CREDIT_ISSUED)
                .time(LocalDateTime.now())
                .changeType(ApplicationStatusHistoryDTO.ChangeTypeEnum.AUTOMATIC));


        applicationRepository.save(application
                .setStatus(ApplicationStatus.CREDIT_ISSUED)
                .setStatusHistory(statusHistory));
        creditRepository.save(credit.setCreditStatus(CreditStatus.ISSUED));

        log.info("\n----------------------------------------" +
                "\n-------------CONGRATULATION!------------" +
                "\n--------CREDIT FOR APPLICATION {}-------" +
                "\n----------------ISSUED------------------" +
                "\n----------------------------------------", applicationId);

    }

    private Integer generateSesCode() {
        int max = 9999;
        int min = 1000;
        return (int) (Math.random() * (max - min + 1) + min);
    }
}