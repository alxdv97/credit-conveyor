package ru.deyev.credit.gateway.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.deyev.credit.gateway.model.ApplicationDTO;

@FeignClient(url = "http://localhost:8082/deal", name = "DEAL-FEIGN-CLIENT")
public interface DealFeignClient {

    @GetMapping("/admin/application/{applicationId}")
    ApplicationDTO getApplicationById(@PathVariable Long applicationId);

}
