package com.casestudy.payment_service.repository;

import com.casestudy.payment_service.model.PayPojo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayRepository extends JpaRepository<PayPojo, Long> {
    String findByRazorpayOrderId(String razorpayOrderId);
}
