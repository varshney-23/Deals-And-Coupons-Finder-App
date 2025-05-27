package org.example.demopayment.repository;

import org.example.demopayment.model.payPOJO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<payPOJO, Long> {
}
