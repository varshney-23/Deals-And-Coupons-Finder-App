package com.dealsandcoupons.payment_service.service;
import com.dealsandcoupons.payment_service.client.CartClient;
import com.dealsandcoupons.payment_service.dto.CartSummaryResponse;
import com.dealsandcoupons.payment_service.dto.CreateOrderRequest;
import com.dealsandcoupons.payment_service.dto.CreateOrderResponse;
import com.dealsandcoupons.payment_service.model.Payment;
import com.dealsandcoupons.payment_service.repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final RazorpayClient razorpayClient;
    private final PaymentRepository paymentRepository;
    @Autowired
    private CartClient cartClient;

    public CreateOrderResponse createOrder(String username) {
        try {
            System.out.println("working-----------------");
            CartSummaryResponse cartSummary = cartClient.getCartDetailsByUsername(username);
            System.out.println("not------------------------------------------");
            double amount = cartSummary.getTotalAmount();
            JSONObject options = new JSONObject();
            options.put("amount", (int)(amount * 100));
            options.put("currency", "INR");
            options.put("receipt", "txn_" + System.currentTimeMillis());

            Order order = razorpayClient.orders.create(options);
            // Save in DB
            Payment payment = Payment.builder()
                    .orderId(order.get("id"))
                    .username(username)
                    .amount(amount)
                    .status(order.get("status"))
                    .build();
            paymentRepository.save(payment);

            return new CreateOrderResponse(order.get("id"), order.get("status"), amount);
        } catch (Exception e) {
            throw new RuntimeException("Payment creation failed", e);
        }

    }

    public String updateStatus(String orderId, String username)
    {
        Payment payment = paymentRepository.findByOrderId(orderId);

        if (payment == null) {
            return "Payment not found for order ID: " + orderId;
        }

        payment.setStatus("SUCCESS");
        paymentRepository.save(payment);

        // Call cart-service to update cart items
        String cartUpdateResponse = cartClient.updateCartStatus(username);

        return "Payment updated & " + cartUpdateResponse;

    }
}