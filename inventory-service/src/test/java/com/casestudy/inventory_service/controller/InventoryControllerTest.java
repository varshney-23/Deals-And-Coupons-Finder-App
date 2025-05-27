package com.casestudy.inventory_service.controller;

import com.casestudy.inventory_service.Interface.IBookingService;
import com.casestudy.inventory_service.Interface.ICouponService;
import com.casestudy.inventory_service.dto.*;
import com.casestudy.inventory_service.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InventoryControllerTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ICouponService couponService;

    @Mock
    private IBookingService bookingService;

    @InjectMocks
    private InventoryController inventoryController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------- Coupon Tests ----------

    @Test
    public void testAddPromoCoupon() {
        PromoCouponDTO dto = new PromoCouponDTO();
        CouponResponseDTO response = new CouponResponseDTO();

        when(couponService.addPromoCoupon(dto)).thenReturn(ResponseEntity.ok(response));

        ResponseEntity<CouponResponseDTO> result = inventoryController.addPromoCoupon(dto);
        assertEquals(200, result.getStatusCodeValue());
        assertEquals(response, result.getBody());
    }

    @Test
    public void testGetAllCoupons() {
        CouponResponseDTO coupon = new CouponResponseDTO();
        List<CouponResponseDTO> list = List.of(coupon);

        when(couponService.getAllCoupons()).thenReturn(ResponseEntity.ok(list));

        ResponseEntity<List<CouponResponseDTO>> result = inventoryController.getAllCoupons();
        assertEquals(200, result.getStatusCodeValue());
        assertEquals(1, result.getBody().size());
    }

    @Test
    public void testDeleteCouponById() {
        Long id = 1L;
        when(couponService.deleteCouponById(id)).thenReturn(ResponseEntity.ok("Deleted"));

        ResponseEntity<String> response = inventoryController.deleteCouponById(id);
        assertEquals("Deleted", response.getBody());
    }

    // ---------- Booking Tests ----------

    @Test
    public void testBookPromotional() {
        Long couponId = 1L;
        String token = "Bearer xyz";
        BookingResponseDTO bookingResponse = new BookingResponseDTO();

        when(bookingService.bookPromotional(couponId, token)).thenReturn(ResponseEntity.ok(bookingResponse));

        ResponseEntity<BookingResponseDTO> result = inventoryController.bookPromotional(couponId, token);
        assertEquals(200, result.getStatusCodeValue());
    }

    @Test
    public void testBookPaid() {
        BookingRequestDTO request = new BookingRequestDTO();
        String token = "Bearer xyz";
        BookingResponseDTO response = new BookingResponseDTO();

        when(bookingService.bookPaidCoupon(request, token)).thenReturn(ResponseEntity.ok(response));

        ResponseEntity<BookingResponseDTO> result = inventoryController.bookPaid(request, token);
        assertEquals(200, result.getStatusCodeValue());
    }

    @Test
    public void testCompletePayment() {
        Long bookingId = 1L;
        when(bookingService.completePayment(bookingId)).thenReturn(ResponseEntity.ok("Payment Done"));

        ResponseEntity<String> result = inventoryController.completePayment(bookingId);
        assertEquals("Payment Done", result.getBody());
    }

    @Test
    public void testGetUserBookings() {
        Long userId = 100L;
        BookingResponseDTO dto = new BookingResponseDTO();
        List<BookingResponseDTO> list = List.of(dto);

        when(bookingService.getUserBookings(userId)).thenReturn(ResponseEntity.ok(list));

        ResponseEntity<List<BookingResponseDTO>> response = inventoryController.getUserBookings(userId);
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testGetAllBookings() {
        BookingResponseDTO dto = new BookingResponseDTO();
        List<BookingResponseDTO> list = List.of(dto);

        when(bookingService.getAllBookings()).thenReturn(ResponseEntity.ok(list));

        ResponseEntity<List<BookingResponseDTO>> response = inventoryController.getAllBookings();
        assertEquals(1, response.getBody().size());
    }
}
