package ru.deyev.credit.gateway.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.deyev.credit.gateway.model.ApplicationDTO;
import ru.deyev.credit.gateway.model.ScoringDataDTO;

@FeignClient(url = "http://localhost:8082/deal", name = "DEAL-FEIGN-CLIENT")
public interface DealFeignClient {

    @GetMapping("/admin/application/{applicationId}")
    ApplicationDTO getApplicationById(@PathVariable Long applicationId);

    @PutMapping("/admin/application/{applicationId}/status")
    void updateApplicationStatusById(@PathVariable Long applicationId, @RequestParam String statusName);

    @PutMapping("/calculate/{applicationId}")
    ResponseEntity<Void> calculateCredit(@PathVariable Long applicationId, @RequestBody ScoringDataDTO scoringData);

    @PostMapping("/document/{applicationId}")
    ResponseEntity<Void> createDocuments(@PathVariable Long applicationId);

    @PostMapping("/document/{applicationId}/send")
    ResponseEntity<Void> sendDocuments(@PathVariable Long applicationId);

    @PostMapping("/document/{applicationId}/sign")
    ResponseEntity<Void> signDocuments(@PathVariable Long applicationId);

    @PostMapping("/document/{applicationId}/code")
    ResponseEntity<Void> verifyCode(@PathVariable Long applicationId, @RequestBody Integer sesCode);
}
