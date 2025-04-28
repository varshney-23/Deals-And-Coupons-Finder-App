package com.casestudy.coupon_service.utils;

import java.util.Random;

public class CouponsCodeGenerator {

    public static String generateCouponCode(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder coupon = new StringBuilder();
        Random rnd = new Random();
        while (coupon.length() < length) {
            int index = (int) (rnd.nextFloat() * characters.length());
            coupon.append(characters.charAt(index));
        }
        return coupon.toString();
    }
}