package com.casestudy.payment_service.controller;

import com.casestudy.payment_service.model.PayRequestDTO;
import com.casestudy.payment_service.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PayController {

    @Autowired
    private PayService paymentService;

    @PostMapping("/process")
    public String processPayment(@RequestBody PayRequestDTO dto) {
        return paymentService.processPayment(dto);
    }
}