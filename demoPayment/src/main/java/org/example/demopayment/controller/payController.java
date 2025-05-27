package org.example.demopayment.controller;


import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.example.demopayment.model.payPOJO;
import org.example.demopayment.repository.OrderRepository;
import com.razorpay.Order;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payment")
public class payController {
    private RazorpayClient razorpayClient;

    @Autowired
    private OrderRepository orderRepository;

    public payController(
            @Value("${razorpay.key_id}") String keyId,
            @Value("${razorpay.key_secret}") String keySecret) throws RazorpayException {
        this.razorpayClient = new RazorpayClient(keyId, keySecret);
    }

    @PostMapping("/create-order")
    public ResponseEntity<String> createOrder(@RequestParam Double amount, @RequestParam String receipt) {
        try {
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", (int) (amount * 100));
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", receipt);
            orderRequest.put("payment_capture", 1);

            Order order = razorpayClient.orders.create(orderRequest);

            payPOJO entity = new payPOJO();
            entity.setRazorpayOrderId(order.get("id"));
            entity.setAmount(amount);
            entity.setCurrency(order.get("currency"));
            entity.setStatus(order.get("status"));
            entity.setReceipt(receipt);
            entity.setCreatedAt(order.get("created_at").toString());

            orderRepository.save(entity);

            return ResponseEntity.ok(order.toString());

        } catch (RazorpayException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }


    @PostMapping("/verify")
    public ResponseEntity<String> verifySignature(@RequestBody Map<String, String> payload) {
        try {
            String orderId = payload.get("order_id");
            String paymentId = payload.get("payment_id");
            String signature = payload.get("razorpay_signature");
            return ResponseEntity.ok("Payment verified successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error verifying payment: " + e.getMessage());
        }
    }
}

