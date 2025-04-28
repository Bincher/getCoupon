package com.bincher.getCoupon.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bincher.getCoupon.dto.ResponseDto;
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

    @PostMapping("/receive")
    public ResponseEntity<? super ReceiveCouponResponseDto> receiveLockCoupon(
        @RequestBody @Valid ReceiveCouponRequestDto requestBody,
        @AuthenticationPrincipal String userId
    ) {
        try {
            // 쿠폰 ID와 사용자 ID를 서비스의 issueCoupon 메서드에 전달
            couponService.issueCoupon(requestBody, userId);
            return ReceiveCouponResponseDto.success();
        } catch (RuntimeException e) {
            // 예외 메시지에 따라 응답 처리
            if (e.getMessage().contains("쿠폰이 모두 소진되었습니다")) {
                return ReceiveCouponResponseDto.insufficientCoupon();
            } else if (e.getMessage().contains("쿠폰 발급 중입니다")) {
                return ReceiveCouponResponseDto.duplicatedCoupon();
            }
            return ResponseDto.databaseError();
        }
    }
}
