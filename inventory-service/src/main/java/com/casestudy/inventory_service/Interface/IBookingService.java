package com.casestudy.inventory_service.Interface;

import com.casestudy.inventory_service.dto.BookingRequestDTO;
import com.casestudy.inventory_service.dto.BookingResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface IBookingService {
    ResponseEntity<BookingResponseDTO> bookPromotional(Long couponId, String token);
    ResponseEntity<BookingResponseDTO> bookPaidCoupon(BookingRequestDTO dto, String token);
    ResponseEntity<String> completePayment(Long bookingId);
    ResponseEntity<List<BookingResponseDTO>> getUserBookings(Long userId);
    ResponseEntity<List<BookingResponseDTO>> getAllBookings();
}

