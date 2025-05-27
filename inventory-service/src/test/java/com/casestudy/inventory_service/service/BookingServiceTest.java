package com.casestudy.inventory_service.service;

import com.casestudy.inventory_service.dto.*;
import com.casestudy.inventory_service.feignClient.PaymentClient;
import com.casestudy.inventory_service.feignClient.ProfileClient;
import com.casestudy.inventory_service.model.Booking;
import com.casestudy.inventory_service.model.Brand;
import com.casestudy.inventory_service.model.Coupons;
import com.casestudy.inventory_service.repository.BookingRepository;
import com.casestudy.inventory_service.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookingServiceTest {

    @InjectMocks
    private BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private ProfileClient profileClient;

    @Mock
    private PaymentClient paymentClient;

    private Booking sampleBooking;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        Brand brand = new Brand();
        brand.setBrandName("Adidas");

        Coupons coupon = new Coupons();
        coupon.setCouponType("paid");
        coupon.setBrand(brand);
        coupon.setPrice(500);

        sampleBooking = new Booking();
        sampleBooking.setBookingId(1L);
        sampleBooking.setCoupon(coupon);
        sampleBooking.setCouponCode("XYZ1234567890");
        sampleBooking.setQuantity(1);
        sampleBooking.setPaid(true);
        sampleBooking.setPrice(500);
        sampleBooking.setBookingTime(LocalDateTime.of(2025, 5,24,10,30));
        
    }

    @Test
    void testBookPromotionalSuccess() {
        Coupons coupon = new Coupons();
        coupon.setCoupon_id(1L);
        coupon.setCouponType("promotional");
        Brand brand = new Brand();
        brand.setBrandName("TestBrand");
        coupon.setBrand(brand);

        User user = new User();
        user.setId(10L);
        user.setEmail("test@email.com");

        when(profileClient.getUserProfile("token")).thenReturn(ResponseEntity.ok(user));
        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));
        when(bookingRepository.findByUserIdAndCouponId(10L, 1L)).thenReturn(Optional.empty());
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArguments()[0]);

        ResponseEntity<BookingResponseDTO> response = bookingService.bookPromotional(1L, "token");
        assertNotNull(response.getBody());
        assertEquals("promotional", response.getBody().getCouponType());
    }

    @Test
    void testBookPaidCouponSuccess() {
        Coupons coupon = new Coupons();
        coupon.setCoupon_id(1L);
        coupon.setCouponType("paid");
        coupon.setPrice(100);
        coupon.setQuantity(10);
        Brand brand = new Brand();
        brand.setBrandName("BrandX");
        coupon.setBrand(brand);

        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setUserId(5L);
        dto.setCouponId(1L);
        dto.setQuantity(2);

        User user = new User();
        user.setId(5L);
        user.setEmail("user@mail.com");
        user.setRole("USER");

        when(profileClient.getUserProfile("token")).thenReturn(ResponseEntity.ok(user));
        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArguments()[0]);

        ResponseEntity<BookingResponseDTO> response = bookingService.bookPaidCoupon(dto, "token");

        assertEquals(200, response.getBody().getPrice());
        assertEquals("BrandX", response.getBody().getCouponBrand());
    }

    @Test
    void testCompletePaymentSuccess() {
        Booking booking = new Booking();
        booking.setBookingId(1L);
        booking.setPrice(300);
        booking.setQuantity(1);
        booking.setEmail("someone@pay.com");
        booking.setPaid(false);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(paymentClient.processPayment(any(PayRequestDTO.class))).thenReturn("PaymentDone");
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArguments()[0]);

        ResponseEntity<String> response = bookingService.completePayment(1L);
        assertTrue(response.getBody().contains("successful"));
    }

    @Test
    void testGetUserBookings() {
        Booking booking = new Booking();
        Coupons coupon = new Coupons();
        coupon.setCouponType("paid");
        Brand brand = new Brand();
        brand.setBrandName("Zara");
        coupon.setBrand(brand);
        booking.setBookingId(2L);
        booking.setUserId(20L);
        booking.setCoupon(coupon);
        booking.setCouponCode("XYZ123");
        booking.setQuantity(1);
        booking.setPaid(true);
        booking.setPrice(100);
        booking.setBookingTime(LocalDateTime.now());

        when(bookingRepository.findByUserId(20L)).thenReturn(List.of(booking));

        ResponseEntity<List<BookingResponseDTO>> result = bookingService.getUserBookings(20L);
        assertEquals(1, result.getBody().size());
        assertEquals("Zara", result.getBody().get(0).getCouponBrand());
    }

    @Test
    void testGetAllBookings_shouldReturnListOfBookingDTOs() {
        when(bookingRepository.findAll()).thenReturn(Arrays.asList(sampleBooking));

        ResponseEntity<List<BookingResponseDTO>> response = bookingService.getAllBookings();

        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getCouponBrand()).isEqualTo("Adidas");

        verify(bookingRepository, times(1)).findAll();
    }

}
