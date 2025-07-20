package com.casestudy.inventory_service.repository;

import com.casestudy.inventory_service.model.BookingGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingGroupRepository extends JpaRepository<BookingGroup, Long> {
}
