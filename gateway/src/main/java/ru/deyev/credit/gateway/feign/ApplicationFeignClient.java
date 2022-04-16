package ru.deyev.credit.gateway.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(url = "http://localhost:8081", name = "APPLICATION-FEIGN-CLIENT")
public interface ApplicationFeignClient {

    @GetMapping("/hello")
    ResponseEntity<String> getTestString();
}
