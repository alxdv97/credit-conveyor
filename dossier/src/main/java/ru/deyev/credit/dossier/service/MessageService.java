package ru.deyev.credit.dossier.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.deyev.credit.dossier.mail.MessageFromKafka;
import ru.deyev.credit.dossier.mail.EmailMessage;

@Service
@AllArgsConstructor
@Slf4j
public class MessageService {

    @Value("${custom.message.file.finish-registration.subject}")
    private final String FINISH_REGISTRATION_SUBJECT;

    @Value("${custom.message.file.finish-registration.text}")
    private final String FINISH_REGISTRATION_TEXT;

    @Value("${custom.message.file.create-document.subject}")
    private final String CREATE_DOCUMENT_SUBJECT;

    @Value("${custom.message.file.create-document.text}")
    private final String CREATE_DOCUMENT_TEXT;

    @Value("${custom.message.file.send-document.subject}")
    private final String SEND_DOCUMENT_SUBJECT;

    @Value("${custom.message.file.send-document.text}")
    private final String SEND_DOCUMENT_TEXT;

    @Value("${custom.message.file.send-ses.subject}")
    private final String SEND_SES_SUBJECT;

    @Value("${custom.message.file.send-ses.text}")
    private final String SEND_SES_TEXT;

    private ObjectMapper objectMapper;

    public MessageFromKafka parseMessageFromKafka(String message) {
        try {
            return objectMapper.readValue(message, MessageFromKafka.class);
        } catch (JsonProcessingException e) {
            log.warn("Cannot parse message \"{}\" to JSON", message);
            throw new RuntimeException(e);
        }
    }

    public EmailMessage KafkaMessageToEmailMessage(MessageFromKafka fromKafka) {
        String subject;
        String text;

        switch (fromKafka.getMessageType()) {
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
                log.warn("Incorrect messageType \"{}\"", fromKafka.getMessageType());
                throw new RuntimeException("Incorrect messageType");
            }
        }

        return new EmailMessage(fromKafka.getAddress(), subject, text);
    }
}
