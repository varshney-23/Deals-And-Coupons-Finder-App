package com.casestudy.inventory_service.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "booking_groups")
public class BookingGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;

    private Long userId;

    private String email;

    private int totalAmount;

    private boolean isPaid;

    private LocalDateTime bookingTime;

    @OneToMany(mappedBy = "bookingGroup", cascade = CascadeType.ALL)
    private List<Booking> bookings;
}
