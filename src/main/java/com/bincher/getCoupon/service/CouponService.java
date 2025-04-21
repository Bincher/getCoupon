package com.bincher.getCoupon.service;

import org.springframework.http.ResponseEntity;

import com.bincher.getCoupon.dto.request.coupon.PostCouponRequestDto;
import com.bincher.getCoupon.dto.request.coupon.ReceiveCouponRequestDto;
import com.bincher.getCoupon.dto.response.coupon.GetCouponListResponseDto;
import com.bincher.getCoupon.dto.response.coupon.PostCouponResponseDto;
import com.bincher.getCoupon.dto.response.coupon.ReceiveCouponResponseDto;

public interface CouponService {
    ResponseEntity<? super GetCouponListResponseDto> getCouponList();
    ResponseEntity<? super PostCouponResponseDto> postCoupon(PostCouponRequestDto dto);
    ResponseEntity<? super ReceiveCouponResponseDto> receiveCoupon(ReceiveCouponRequestDto dto, String userId);
}
