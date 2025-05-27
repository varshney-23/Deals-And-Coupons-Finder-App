package com.casestudy.payment_service.service;

import com.casestudy.payment_service.exception.PaymentException;
import com.casestudy.payment_service.model.PayPojo;
import com.casestudy.payment_service.model.PayRequestDTO;
import com.casestudy.payment_service.repository.PayRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PayService {

    @Autowired
    private RazorpayClient razorpayClient;

    @Autowired
    private PayRepository paymentRepository;

    public String processPayment(PayRequestDTO req) {

        try {
            double amountInPaise = req.getAmount() * req.getQuantity() * 100;
            JSONObject opt = new JSONObject();
            opt.put("amount", amountInPaise);
            opt.put("currency", "INR");
            opt.put("receipt", req.getReceipt());

            Order order = razorpayClient.orders.create(opt);

            PayPojo payPojo = new PayPojo();
            payPojo.setAmount(req.getAmount());
            payPojo.setEmail(req.getEmail());
            payPojo.setOrderStatus("CREATED");
            payPojo.setRazorpayOrderId(order.get("id"));

            paymentRepository.save(payPojo);

            return "Payment Order Created: " + order.get("id");

        } catch (Exception e) {
            throw new PaymentException("Payment failed: "+e.getMessage());
        }
    }
}
