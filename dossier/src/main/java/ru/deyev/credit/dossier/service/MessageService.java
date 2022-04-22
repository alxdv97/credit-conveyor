package ru.deyev.credit.dossier.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.deyev.credit.dossier.mail.EmailMessage;
import ru.deyev.credit.dossier.mail.MessageFromKafka;
import ru.deyev.credit.dossier.sender.EmailSender;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageService {

    @Value("${custom.message.finish-registration.subject}")
    private String FINISH_REGISTRATION_SUBJECT;

    @Value("${custom.message.finish-registration.text}")
    private String FINISH_REGISTRATION_TEXT;

    @Value("${custom.message.create-document.subject}")
    private String CREATE_DOCUMENT_SUBJECT;

    @Value("${custom.message.create-document.text}")
    private String CREATE_DOCUMENT_TEXT;

    @Value("${custom.message.send-document.subject}")
    private String SEND_DOCUMENT_SUBJECT;

    @Value("${custom.message.send-document.text}")
    private String SEND_DOCUMENT_TEXT;

    @Value("${custom.message.send-ses.subject}")
    private String SEND_SES_SUBJECT;

    @Value("${custom.message.send-ses.text}")
    private String SEND_SES_TEXT;

    private final ObjectMapper objectMapper;

    private final EmailSender emailSender;

    public MessageFromKafka parseMessageFromJSON(String messageJSON) {
        try {
            log.info("MessageService.parseMessageFromKafka() received message \"{}\"", messageJSON);
            MessageFromKafka messageFromKafka = objectMapper.readValue(messageJSON, MessageFromKafka.class);
            log.info("MessageService.parseMessageFromKafka() parsed message \"{}\"", messageFromKafka);
            return messageFromKafka;
        } catch (JsonProcessingException e) {
            log.warn("Cannot parse messageJSON \"{}\" to JSON", messageJSON);
            throw new RuntimeException(e);
        }
    }

    public EmailMessage kafkaMessageToEmailMessage(MessageFromKafka fromKafka) {
        String subject;
        String text;

        switch (fromKafka.getTheme()) {
            case FINISH_REGISTRATION: {
                subject = FINISH_REGISTRATION_SUBJECT;
                text = FINISH_REGISTRATION_TEXT;
                break;
            }
            case CREATE_DOCUMENT: {
                subject = CREATE_DOCUMENT_SUBJECT;
                text = CREATE_DOCUMENT_TEXT;
                break;
            }
            case SEND_DOCUMENT: {
                subject = SEND_DOCUMENT_SUBJECT;
                text = SEND_DOCUMENT_TEXT;
                break;
            }
            case SEND_SES: {
                subject = SEND_SES_SUBJECT;
                text = SEND_SES_TEXT;
                break;
            }
            default: {
                log.warn("Incorrect messageType \"{}\"", fromKafka.getTheme());
                throw new RuntimeException("Incorrect messageType");
            }
        }

        return new EmailMessage(fromKafka.getAddress(), subject, text);
    }

    public void sendMessage(MessageFromKafka messageFromKafka) {
        log.info("MessageService.sendMessage() send message \"{}\"", messageFromKafka);
        emailSender.sendMessage(kafkaMessageToEmailMessage(messageFromKafka));
    }
}
