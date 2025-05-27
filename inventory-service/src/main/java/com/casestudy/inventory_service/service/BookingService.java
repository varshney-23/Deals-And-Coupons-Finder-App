package com.casestudy.inventory_service.service;

import com.casestudy.inventory_service.Interface.IBookingService;
import com.casestudy.inventory_service.dto.BookingRequestDTO;
import com.casestudy.inventory_service.dto.BookingResponseDTO;
import com.casestudy.inventory_service.Exception.UserException;
import com.casestudy.inventory_service.dto.PayRequestDTO;
import com.casestudy.inventory_service.dto.User;
import com.casestudy.inventory_service.feignClient.PaymentClient;
import com.casestudy.inventory_service.feignClient.ProfileClient;
import com.casestudy.inventory_service.model.Booking;
import com.casestudy.inventory_service.model.Coupons;
import com.casestudy.inventory_service.repository.BookingRepository;
import com.casestudy.inventory_service.repository.CouponRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class BookingService implements IBookingService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private ProfileClient profileClient;

    @Autowired
    private PaymentClient paymentClient;


    @Override
    @CircuitBreaker(name = "profileClientCB", fallbackMethod = "fallbackForBookPromotional")
    public ResponseEntity<BookingResponseDTO> bookPromotional(Long couponId, String token) {
        //getting user details from token
        User user = profileClient.getUserProfile(token).getBody();

        //checking if user is valid
        if (user == null) {
            throw new UserException("User not valid");
        }

        Long userId = user.getId();

        //if coupon id is correct
        Coupons coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new UserException("Coupon not found"));

        //for checking apis only => if coupontype is valid in correspondance with couponId.
        if (!coupon.getCouponType().equalsIgnoreCase("promotional")) {
            throw new UserException("Not a promotional coupon!");
        }

        //if user has already availed the coupon or not
        Optional<Booking> exists = bookingRepository.findByUserIdAndCouponId(userId, couponId);
        if(exists.isPresent()){
            throw new UserException("You have already availed this promotional coupon.");
        }

        //save booking!
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setCoupon(coupon);
        booking.setCouponCode(generateCode2());
        booking.setQuantity(1);
        booking.setPrice(0);
        booking.setPaid(false);
        booking.setEmail(user.getEmail());
        booking.setBookingTime(LocalDateTime.now());

        Booking saved = bookingRepository.save(booking);

        return ResponseEntity.ok(convertToDTO(saved));
    }

    public ResponseEntity<BookingResponseDTO> fallbackForBookPromotional(Long userId, Long couponId, Throwable t) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
    }

    @Override
    @CircuitBreaker(name = "profileClientCB", fallbackMethod = "fallbackForBookPaidCoupon")
    public ResponseEntity<BookingResponseDTO> bookPaidCoupon(BookingRequestDTO dto, String token) {
        //getting user from the token!
        User user = profileClient.getUserProfile(token).getBody();

        //if user is valid!
        if (user == null || !user.getId().equals(dto.getUserId())) {
            throw new UserException("User not valid");
        }

        //checking if booking is done by user or admin
        if (!"USER".equalsIgnoreCase(user.getRole())) {
            throw new UserException("Only users with role 'USER' can book coupons");
        }

        //if coupon id exists.
        Coupons coupon = couponRepository.findById(dto.getCouponId())
                .orElseThrow(() -> new UserException("Coupon not found"));

        //if id ke through coupon type doesn't match =>
        if (!coupon.getCouponType().equalsIgnoreCase("paid")) {
            throw new UserException("Not a paid coupon!");
        }

        //if coupon amount is less than bought coupon
        if(coupon.getQuantity() < dto.getQuantity()){
            throw new UserException("Only "+coupon.getQuantity()+" coupons left!");
        }

        //save booking in booking db!
        Booking booking = new Booking();
        booking.setUserId(user.getId());
        booking.setCoupon(coupon);
        booking.setQuantity(dto.getQuantity());
        booking.setPaid(false);
        booking.setPrice(coupon.getPrice() * dto.getQuantity());
        booking.setEmail(user.getEmail());
        booking.setBookingTime(LocalDateTime.now());

        Booking saved = bookingRepository.save(booking);

        //decreasing the no. of coupons for that particular coupon!
        if(coupon.getQuantity() > 0 && coupon.getQuantity() >= dto.getQuantity()) {
            coupon.setQuantity(coupon.getQuantity() - dto.getQuantity());
            couponRepository.save(coupon);
        }

        BookingResponseDTO newDTO = convertToDTO(saved);
        newDTO.setEmail(user.getEmail());

        return ResponseEntity.ok(newDTO);
    }

    public ResponseEntity<BookingResponseDTO> fallbackForBookPaidCoupon(BookingRequestDTO dto, String token, Throwable t) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(null); // or custom error dto
    }

    @Override
    @CircuitBreaker(name = "paymentClientCB", fallbackMethod = "fallbackForCompletePayment")
    public ResponseEntity<String> completePayment(Long bookingId) {
//        System.out.println("payment fxn ke inside!!");
        //if booking even exists
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new UserException("Booking not found"));

        //if booking is already done, like we won't be having to check this one to payment-service.
        if (booking.isPaid()) return ResponseEntity.ok("Already paid");

//        System.out.println("check krne ke baad");

        PayRequestDTO payRequestDTO = new PayRequestDTO();
        payRequestDTO.setAmount(booking.getPrice());
        payRequestDTO.setQuantity(booking.getQuantity());
        payRequestDTO.setEmail(booking.getEmail());
        payRequestDTO.setReceipt("txn_"+bookingId);

        try{
//            System.out.println("payclient ko call krne se pehle");
            String response = paymentClient.processPayment(payRequestDTO);
//            System.out.println("payclient ko call krne ke baad");
            booking.setPaid(true);
            bookingRepository.save(booking);
//            System.out.println("Save ogy________________");
            return ResponseEntity.ok("Payment successful and booking updated!\n"+ response);
        } catch(Exception e) {
//            System.out.println("Kya exception catch hua?");
            throw new UserException("Payment failed" + e.getMessage());
        }
    }

    public ResponseEntity<String> fallbackForCompletePayment(Long bookingId, Throwable t) {
//        System.out.println("service is down, Ma'am, idk why?");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Payment service is temporarily down. Please try again later.");
    }

    @Override
    public ResponseEntity<List<BookingResponseDTO>> getUserBookings(Long userId) {
        List<Booking> bookings = bookingRepository.findByUserId(userId);
        List<BookingResponseDTO> result = bookings.stream()
                .map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        return ResponseEntity.ok(bookings.stream().map(this::convertToDTO).collect(Collectors.toList()));
    }

    private BookingResponseDTO convertToDTO(Booking booking) {
        return new BookingResponseDTO(
                booking.getBookingId(),
                booking.getCoupon().getBrand().getBrandName(),
                booking.getCoupon().getCouponType(),
                booking.getCouponCode(),
                booking.getQuantity(),
                booking.isPaid(),
                booking.getPrice(),
                booking.getBookingTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        );
    }

//    private String generateCode() {
//        return java.util.UUID.randomUUID().toString().substring(0, 12).toUpperCase();
//    }

    private String generateCode2() {
        StringBuilder code = new StringBuilder(15);
        for (int i = 0; i < 15; i++) {
             int index = RANDOM.nextInt(CHARACTERS.length());
             code.append(CHARACTERS.charAt(index));
         }
         return code.toString();
    }

}
