package com.casestudy.coupon_service.feignClient;

import com.casestudy.coupon_service.dto.ProductRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductClient {

    @PostMapping("/products/admin/add")
    public ResponseEntity<String> addBrand(@RequestBody ProductRequestDTO productRequestDTO);
}