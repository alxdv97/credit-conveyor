package ru.deyev.credit.dossier.mail;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageFromKafka {
    private String address;
    private MessageType messageType;
}
