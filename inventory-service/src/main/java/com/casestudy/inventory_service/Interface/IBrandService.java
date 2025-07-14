package com.casestudy.inventory_service.Interface;
import com.casestudy.inventory_service.dto.BrandResponseDTO;
import com.casestudy.inventory_service.model.Brand;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IBrandService {
    ResponseEntity<List<BrandResponseDTO>> getAllBrands();
}
