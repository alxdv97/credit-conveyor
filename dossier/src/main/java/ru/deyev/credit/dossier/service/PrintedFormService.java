package ru.deyev.credit.dossier.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.deyev.credit.dossier.feign.DealFeignClient;
import ru.deyev.credit.dossier.mail.MessageType;
import ru.deyev.credit.dossier.model.ApplicationDTO;

import java.io.File;
import java.util.List;

@Service
@AllArgsConstructor
public class PrintedFormService {

    private DealFeignClient dealFeignClient;

    public List<File> createDocuments(MessageType type, Long applicationId) {
        ApplicationDTO application = dealFeignClient.getApplicationById(applicationId);
        return null;
    }

    public File createCreditContract(ApplicationDTO application) {
        return null;
    }
}
