package com.bincher.getCoupon.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bincher.getCoupon.dto.response.coupon.GetCouponListResponseDto;
import com.bincher.getCoupon.service.CouponService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/v1/coupon")
@RequiredArgsConstructor
public class CouponController {
    
    private final CouponService couponService;

    @GetMapping("")
    public ResponseEntity<? super GetCouponListResponseDto> getCouponList(){
        ResponseEntity<? super GetCouponListResponseDto> response = couponService.getCouponList();
        return response;
    }
    
}
