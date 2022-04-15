package ru.deyev.credit.gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import ru.deyev.credit.gateway.api.ApplicationApi;
import ru.deyev.credit.gateway.api.TestApi;
import ru.deyev.credit.gateway.model.LoanOfferDTO;

import java.util.List;
import java.util.Optional;

@RestController
public class ApplicationController implements TestApi, ApplicationApi {

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return TestApi.super.getRequest();
    }

    @Override
    public ResponseEntity<List<LoanOfferDTO>> test() {
        return ResponseEntity.ok(List.of(new LoanOfferDTO().amount(1L)));
    }

}
