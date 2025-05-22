package com.casestudy.inventory_service.feignClient;

import com.casestudy.inventory_service.dto.PayRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("PAYMENT-SERVICE")
public interface PaymentClient {

    @PostMapping("/payment/process")
    String processPayment(@RequestBody PayRequestDTO payRequestDTO);

}