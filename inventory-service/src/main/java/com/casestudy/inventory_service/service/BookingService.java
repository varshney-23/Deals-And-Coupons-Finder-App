package com.casestudy.inventory_service.service;

import com.casestudy.inventory_service.Interface.IBookingService;
import com.casestudy.inventory_service.dto.*;
import com.casestudy.inventory_service.Exception.UserException;
import com.casestudy.inventory_service.feignClient.PaymentClient;
import com.casestudy.inventory_service.feignClient.ProfileClient;
import com.casestudy.inventory_service.model.Booking;
import com.casestudy.inventory_service.model.BookingGroup;
import com.casestudy.inventory_service.model.Coupons;
import com.casestudy.inventory_service.repository.BookingGroupRepository;
import com.casestudy.inventory_service.repository.BookingRepository;
import com.casestudy.inventory_service.repository.CouponRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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

    @Autowired
    private BookingGroupRepository bookingGroupRepository;


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

        String coupon1 = generateCode2();
        System.out.println(coupon1);
        //save booking!
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setCoupon(coupon);
        booking.setCouponCode(coupon1);
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
    public ResponseEntity<Map<String, Object>> bookMultiplePaidCoupons(MultiCouponBookingRequestDTO request, String token) {

        log.info("Calling Profile Service with token: {}", token);
        User user = profileClient.getUserProfile(token).getBody();
        log.info("Received user: {}", user);
//        log.info(String.valueOf(user.getId()));
//        log.info(String.valueOf(request.getUserId()));
        if (user == null || !user.getId().equals(request.getUserId())) {
            throw new UserException("Invalid user");
        }
        log.info("debugger1");
        if (!"USER".equalsIgnoreCase(user.getRole())) {
            throw new UserException("Only users can book coupons");
        }
        log.info("debugger2");
        List<Booking> bookings = new ArrayList<>();
        int totalAmount = 0;

        log.info("before..");

        for (MultiCouponBookingRequestDTO.CouponBookingData data : request.getCoupons()) {
            Coupons coupon = couponRepository.findById(data.getCouponId())
                    .orElseThrow(() -> new UserException("Coupon ID " + data.getCouponId() + " not found"));

            if (!"paid".equalsIgnoreCase(coupon.getCouponType())) {
                throw new UserException("Coupon ID " + coupon.getCoupon_id() + " is not a paid coupon");
            }

            if (coupon.getQuantity() < data.getQuantity()) {
                throw new UserException("Only " + coupon.getQuantity() + " coupons left for ID " + coupon.getCoupon_id());
            }

            Booking booking = new Booking();
            booking.setUserId(user.getId());
            booking.setCoupon(coupon);
            booking.setQuantity(data.getQuantity());
            booking.setPrice(coupon.getPrice() * data.getQuantity());
            booking.setEmail(user.getEmail());
            booking.setPaid(false);
            booking.setBookingTime(LocalDateTime.now());

            totalAmount += booking.getPrice();
            bookings.add(booking);
        }

        log.info("before2");

        if (totalAmount < 100) {
            throw new UserException("Payment amount must be at least ₹1 (i.e., 100 paise)");
        }

        BookingGroup group = new BookingGroup();
        group.setUserId(user.getId());
        group.setEmail(user.getEmail());
        group.setPaid(false);
        group.setBookingTime(LocalDateTime.now());
        group.setTotalAmount(totalAmount);

        group.setBookings(bookings);
        bookings.forEach(b -> b.setBookingGroup(group));

        log.info("before3");

        bookingGroupRepository.save(group); // auto saves bookings due to cascade

//        log.info("Final group response: {}", group);

//        return ResponseEntity.ok("Booking group created successfully. Group ID: " + group.getGroupId() + group.getTotalAmount());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Booking group created successfully");
        response.put("groupId", group.getGroupId());
        response.put("totalAmount", group.getTotalAmount()); // 🔥 Important!

        log.info(String.valueOf(group.getTotalAmount()));

        return ResponseEntity.ok(response);

    }

    public ResponseEntity<String> fallbackForBookPaidCoupon(MultiCouponBookingRequestDTO request, String token,  Throwable t) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(null); // or custom error dto
    }

    @Override
    @CircuitBreaker(name = "paymentClientCB", fallbackMethod = "fallbackForCompletePayment")
    public ResponseEntity<String> completeGroupPayment(Long groupId) {

        BookingGroup group = bookingGroupRepository.findById(groupId)
                .orElseThrow(() -> new UserException("Booking group not found"));

        if (group.isPaid()) {
            return ResponseEntity.ok("Already paid");
        }

        PayRequestDTO dto = new PayRequestDTO();
        dto.setAmount(group.getTotalAmount());
        dto.setEmail(group.getEmail());
        dto.setReceipt("txn_group_" + groupId);

        try {
            log.info("Total booking amount to be charged: {}", group.getTotalAmount());
            String paymentResult = paymentClient.processPayment(dto);

            for (Booking booking : group.getBookings()) {
                Coupons coupon = booking.getCoupon();
                int newQty = coupon.getQuantity() - booking.getQuantity();
                if (newQty < 0) throw new UserException("Insufficient quantity for coupon ID " + coupon.getCoupon_id());
                coupon.setQuantity(newQty);
                booking.setPaid(true);
            }

            group.setPaid(true);
            bookingGroupRepository.save(group);

            return ResponseEntity.ok("Group payment successful\n" + paymentResult);
        } catch (Exception e) {
            throw new UserException("Payment failed: " + e.getMessage());
        }
    }

    public ResponseEntity<String> fallbackForCompletePayment(Long groupId, Throwable t) {
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
        StringBuilder code = new StringBuilder(12);
        for (int i = 0; i < 12; i++) {
             int index = RANDOM.nextInt(CHARACTERS.length());
             code.append(CHARACTERS.charAt(index));
         }
         return code.toString();
    }

    @Override
    public ResponseEntity<List<ReceiptCouponDTO>> getPaidBookings(String token) {
        User user = profileClient.getUserProfile(token).getBody();
        assert user != null;
        Long userId = user.getId();
        List<ReceiptCouponDTO> paidBookings = new ArrayList<>();
        for(Booking bb : bookingRepository.findByUserIdAndIsPaid(userId, true)){
            paidBookings.add(convertToReceipt(bb));
        }
        return new ResponseEntity<>(paidBookings, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<ReceiptCouponDTO>> getPromoBookings(String token) {
        User user = profileClient.getUserProfile(token).getBody();
        assert user != null;
        Long userId = user.getId();
        List<ReceiptCouponDTO> promoBookings = new ArrayList<>();
        for(Booking bb : bookingRepository.findByUserIdAndIsPaid(userId, false)){
            promoBookings.add(convertToReceipt(bb));
        }
        return new ResponseEntity<>(promoBookings, HttpStatus.OK);
    }

    private ReceiptCouponDTO convertToReceipt(Booking booking){
        return new ReceiptCouponDTO(
                booking.getBookingId(),
                booking.getUserId(),
                booking.getPrice(),
                booking.getCoupon().getOfferDetails(),
                booking.getQuantity(),
                booking.getCouponCode(),
                booking.isPaid(),
                booking.getBookingTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                booking.getCoupon().getBrandLogo()
        );
    }
}
