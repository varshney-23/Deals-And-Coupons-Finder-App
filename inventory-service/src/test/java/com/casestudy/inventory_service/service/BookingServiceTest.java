//package com.casestudy.inventory_service.service;
//
//import com.casestudy.inventory_service.Exception.UserException;
//import com.casestudy.inventory_service.dto.BookingRequestDTO;
//import com.casestudy.inventory_service.dto.BookingResponseDTO;
//import com.casestudy.inventory_service.dto.User;
//import com.casestudy.inventory_service.feignClient.PaymentClient;
//import com.casestudy.inventory_service.feignClient.ProfileClient;
//import com.casestudy.inventory_service.model.Booking;
//import com.casestudy.inventory_service.model.Brand;
//import com.casestudy.inventory_service.model.Coupons;
//import com.casestudy.inventory_service.repository.BookingRepository;
//import com.casestudy.inventory_service.repository.CouponRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.ResponseEntity;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class BookingServiceTest {
//
//    @Mock
//    private BookingRepository bookingRepository;
//
//    @Mock
//    private CouponRepository couponRepository;
//
//    @Mock
//    private ProfileClient profileClient;
//
//    @Mock
//    private PaymentClient paymentClient;
//
//    @InjectMocks
//    private BookingService bookingService;
//
//    @BeforeEach
//    void setup() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testBookPromotionalSuccess() {
//        Long userId = 1L;
//        Long couponId = 2L;
//        Coupons coupon = new Coupons();
//        coupon.setCouponType("promotional");
//        coupon.setBrand(new Brand());
//
//        when(profileClient.userExists(userId)).thenReturn(true);
//        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
//        when(bookingRepository.save(any())).thenAnswer(i -> i.getArgument(0));
//
//        ResponseEntity<BookingResponseDTO> response = bookingService.bookPromotional(couponId, "token");
//
//        assertEquals(200, response.getStatusCodeValue());
//        assertNotNull(response.getBody());
//    }
//
//    @Test
//    void testBookPaidCouponSuccess() {
//        BookingRequestDTO dto = new BookingRequestDTO();
//        dto.setUserId(1L);
//        dto.setCouponId(2L);
//        dto.setQuantity(3);
//
//        Coupons coupon = new Coupons();
//        coupon.setCouponType("paid");
//        Brand brand = new Brand();
//        brand.setBrandName("BrandX");
//        coupon.setBrand(brand);
//
//        User user = new User();
//        user.setId(1L);
//        user.setRole("USER");
//        user.setEmail("user@example.com");
//
//        when(profileClient.getUserProfile("token")).thenReturn(ResponseEntity.ok(user));
//        when(couponRepository.findById(2L)).thenReturn(Optional.of(coupon));
//        when(bookingRepository.save(any())).thenAnswer(i -> i.getArgument(0));
//
//        ResponseEntity<BookingResponseDTO> response = bookingService.bookPaidCoupon(dto, "token");
//
//        assertEquals(200, response.getStatusCodeValue());
//        assertNotNull(response.getBody());
//        assertEquals("BrandX", response.getBody().getCouponBrand());
//    }
//
////    @Test
////    void testCompletePaymentSuccess() {
////        Booking booking = new Booking();
////        booking.setPaid(false);
////        booking.setBookingTime(LocalDateTime.now());
////
////        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
////        when(paymentClient.processPayment()).thenReturn("Success");
////
////        ResponseEntity<String> response = bookingService.completePayment(1L);
////
////        assertEquals("Payment successful and booking updated!\n", response.getBody());
////        verify(bookingRepository, times(1)).save(any());
////    }
//
//    @Test
//    void testGetUserIdByBookingIdSuccess() {
//        Booking booking = new Booking();
//        booking.setUserId(123L);
//
//        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
//
//        ResponseEntity<Long> response = bookingService.getUserIdByBookingId(1L);
//        assertEquals(123L, response.getBody());
//    }
//
//    @Test
//    void testBookPromotionalThrowsUserExceptionWhenUserNotFound() {
//        when(profileClient.userExists(1L)).thenReturn(false);
//        UserException ex = assertThrows(UserException.class,
//                () -> bookingService.bookPromotional(1L,"token"));
//        assertTrue(ex.getMessage().contains("Invalid user ID"));
//    }
//}
