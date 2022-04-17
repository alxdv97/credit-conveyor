package ru.deyev.credit.gateway.client;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@AllArgsConstructor
public class DealClient {

    private static final String DEAL_URL = "http://localhost:8081";

    private final RestTemplate restTemplate;


}
