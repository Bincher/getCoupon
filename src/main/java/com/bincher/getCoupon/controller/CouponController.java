package com.bincher.getCoupon.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bincher.getCoupon.dto.request.coupon.PostCouponRequestDto;
import com.bincher.getCoupon.dto.request.coupon.ReceiveCouponRequestDto;
import com.bincher.getCoupon.dto.response.coupon.GetCouponListResponseDto;
import com.bincher.getCoupon.dto.response.coupon.GetCouponResponseDto;
import com.bincher.getCoupon.dto.response.coupon.PostCouponResponseDto;
import com.bincher.getCoupon.dto.response.coupon.ReceiveCouponResponseDto;
import com.bincher.getCoupon.service.CouponService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


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
    
    @PostMapping("/admin")
    public ResponseEntity<? super PostCouponResponseDto> postCoupon(
        @RequestBody @Valid PostCouponRequestDto requestBody,
        @AuthenticationPrincipal String id
    ){
        ResponseEntity<? super PostCouponResponseDto> response = couponService.postCoupon(requestBody);
        return response;
    }

    @PostMapping("/event")
    public ResponseEntity<? super ReceiveCouponResponseDto> receiveCoupon(
        @RequestBody @Valid ReceiveCouponRequestDto requestBody,
        @AuthenticationPrincipal String id
    ){
        ResponseEntity<? super ReceiveCouponResponseDto> response = couponService.receiveCoupon(requestBody, id);
        return response;
    }

    @GetMapping("/{couponId}")
    public ResponseEntity<? super GetCouponResponseDto> getCoupon(
        @PathVariable("couponId") String couponId
    ){
        ResponseEntity<? super GetCouponResponseDto> response = couponService.getCoupon(couponId);
        return response;
    }
}
