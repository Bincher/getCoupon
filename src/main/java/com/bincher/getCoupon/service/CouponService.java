package com.bincher.getCoupon.service;

import org.springframework.http.ResponseEntity;

import com.bincher.getCoupon.dto.response.coupon.GetCouponListResponseDto;

public interface CouponService {
    ResponseEntity<? super GetCouponListResponseDto> getCouponList();
}
