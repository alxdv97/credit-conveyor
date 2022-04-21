package ru.deyev.credit.gateway.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(url = "http://localhost:8082/deal", name = "DEAL-FEIGN-CLIENT")
public interface DealFeignClient {

}
