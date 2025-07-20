package com.casestudy.inventory_service.Interface;

import com.casestudy.inventory_service.dto.BookingRequestDTO;
import com.casestudy.inventory_service.dto.BookingResponseDTO;
import com.casestudy.inventory_service.dto.MultiCouponBookingRequestDTO;
import com.casestudy.inventory_service.dto.ReceiptCouponDTO;
import com.casestudy.inventory_service.model.Booking;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;


public interface IBookingService {
    ResponseEntity<BookingResponseDTO> bookPromotional(Long couponId, String token);
    ResponseEntity<List<BookingResponseDTO>> getUserBookings(Long userId);
    ResponseEntity<List<BookingResponseDTO>> getAllBookings();
    ResponseEntity<List<ReceiptCouponDTO>> getPaidBookings(String token);
    ResponseEntity<List<ReceiptCouponDTO>> getPromoBookings(String token);
    ResponseEntity<String> completeGroupPayment(Long groupId);
    ResponseEntity<Map<String, Object>> bookMultiplePaidCoupons(MultiCouponBookingRequestDTO request, String token);

}

