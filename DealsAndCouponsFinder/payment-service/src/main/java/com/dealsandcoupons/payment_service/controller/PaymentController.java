package com.dealsandcoupons.payment_service.controller;

import com.dealsandcoupons.payment_service.dto.CreateOrderResponse;
import com.dealsandcoupons.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
//@CrossOrigin(origins = "http://localhost:63342")
@RequiredArgsConstructor
public class PaymentController {
    @Autowired
    PaymentService paymentService;

    @PostMapping("/create/{username}")
    public CreateOrderResponse createPayment(@PathVariable String username ){
        return paymentService.createOrder(username);
    }

    @PutMapping("/confirmPayment/{orderId}/{username}")
    public String updateStatus(@PathVariable String orderId, @PathVariable String username){
        return paymentService.updateStatus(orderId, username);
    }
}