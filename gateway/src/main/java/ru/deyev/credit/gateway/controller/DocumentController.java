package ru.deyev.credit.gateway.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.deyev.credit.gateway.api.DocumentApi;
import ru.deyev.credit.gateway.service.DocumentService;

@RestController
@AllArgsConstructor
public class DocumentController implements DocumentApi {

    private final DocumentService documentService;

    @Override
    public ResponseEntity<Void> createDocuments() {
        return DocumentApi.super.createDocuments();
    }

    @Override
    public ResponseEntity<Void> sendSesCode(Integer body) {
        return DocumentApi.super.sendSesCode(body);
    }

    @Override
    public ResponseEntity<Void> signDocuments() {
        return DocumentApi.super.signDocuments();
    }
}
