package com.casestudy.coupon_service.service;

import com.casestudy.coupon_service.model.Coupon;
import com.casestudy.coupon_service.repository.CouponRepository;

import java.util.*;

public abstract class InMemoryCouponRepository implements CouponRepository {

    private Map<Long, Coupon> db = new HashMap<>();

    @Override
    public List<Coupon> findByBrand(String brand) {
        List<Coupon> result = new ArrayList<>();
        for (Coupon c : db.values()) {
            if (c.getBrand().equalsIgnoreCase(brand)) {
                result.add(c);
            }
        }
        return result;
    }

    @Override
    public List<Coupon> findByCategory(String category) {
        List<Coupon> result = new ArrayList<>();
        for (Coupon c : db.values()) {
            if (c.getCategory().equalsIgnoreCase(category)) {
                result.add(c);
            }
        }
        return result;
    }

    @Override
    public List<Coupon> findAll() {
        return new ArrayList<>(db.values());
    }

    @Override
    public Optional<Coupon> findById(Long id) {
        return Optional.ofNullable(db.get(id));
    }

    @Override
    public Coupon save(Coupon coupon) {
        db.put(coupon.getCouponId(), coupon);
        return coupon;
    }

    @Override
    public void deleteById(Long id) {
        db.remove(id);
    }
}