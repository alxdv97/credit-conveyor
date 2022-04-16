package ru.deyev.credit.gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.deyev.credit.gateway.api.DocumentApi;

@RestController
public class DocumentController implements DocumentApi {
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
