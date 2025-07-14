package com.casestudy.inventory_service.service;

import com.casestudy.inventory_service.Interface.IBrandService;
import com.casestudy.inventory_service.dto.BookingResponseDTO;
import com.casestudy.inventory_service.dto.BrandResponseDTO;
import com.casestudy.inventory_service.model.Booking;
import com.casestudy.inventory_service.model.Brand;
import com.casestudy.inventory_service.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class BrandService implements IBrandService {

    @Autowired
    public BrandRepository brandRepository;

    public ResponseEntity<List<BrandResponseDTO>> getAllBrands(){
        List<Brand> ll = brandRepository.findAll();
        List<BrandResponseDTO> finall = new ArrayList<>();
        for(Brand bd : ll){
            finall.add(convertDTOs(bd));
        }
        return ResponseEntity.ok(finall);
    }

    private BrandResponseDTO convertDTOs(Brand brand){
        return new BrandResponseDTO(brand.getBrandName(), brand.getBrandLogo());
    }
}
