package ru.deyev.credit.dossier.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.deyev.credit.dossier.mail.EmailMessage;
import ru.deyev.credit.dossier.mail.MessageFromKafka;
import ru.deyev.credit.dossier.sender.EmailSender;
import ru.deyev.credit.dossier.service.MessageService;

@Component
@Slf4j
@AllArgsConstructor
public class KafkaConsumer {

    private EmailSender emailSender;
    private MessageService messageService;

    @KafkaListener(topics = "conveyor-finish-registration", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeFinishRegistrationMessage(String message) {
        log.info("Consume finish registration message from kafka: {}", message);

        MessageFromKafka messageFromKafka = messageService.parseMessageFromJSON(message);
        log.info("MessageFromKafka = {}", messageFromKafka);

        EmailMessage emailMessage = messageService.kafkaMessageToEmailMessage(messageFromKafka);
        log.info("EmailMessage = {}", emailMessage);

        emailSender.sendMessage(emailMessage.getAddress(), emailMessage.getSubject(), emailMessage.getText());
    }

    @KafkaListener(topics = "conveyor-create-documents", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeCreateDocumentsMessage(String message) {
        log.info("Consume create documents message from kafka: {}", message);

        MessageFromKafka messageFromKafka = messageService.parseMessageFromJSON(message);
        log.info("MessageFromKafka = {}", messageFromKafka);

        EmailMessage emailMessage = messageService.kafkaMessageToEmailMessage(messageFromKafka);
        log.info("EmailMessage = {}", emailMessage);

        emailSender.sendMessage(emailMessage.getAddress(), emailMessage.getSubject(), emailMessage.getText());
    }

    @KafkaListener(topics = "conveyor-send-documents", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeSendDocumentsMessage(String message) {
        log.info("Consume send documents message from kafka: {}", message);

        MessageFromKafka messageFromKafka = messageService.parseMessageFromJSON(message);
        log.info("MessageFromKafka = {}", messageFromKafka);

        EmailMessage emailMessage = messageService.kafkaMessageToEmailMessage(messageFromKafka);
        log.info("EmailMessage = {}", emailMessage);

        emailSender.sendMessage(emailMessage.getAddress(), emailMessage.getSubject(), emailMessage.getText());
    }

    @KafkaListener(topics = "conveyor-send-ses", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeSendSesMessage(String message) {
        log.info("Consume send ses message from kafka: {}", message);

        MessageFromKafka messageFromKafka = messageService.parseMessageFromJSON(message);
        log.info("MessageFromKafka = {}", messageFromKafka);

        EmailMessage emailMessage = messageService.kafkaMessageToEmailMessage(messageFromKafka);
        log.info("EmailMessage = {}", emailMessage);

        emailSender.sendMessage(emailMessage.getAddress(), emailMessage.getSubject(), emailMessage.getText());
    }

}
