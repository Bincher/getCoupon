package com.bincher.getCoupon.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bincher.getCoupon.common.ResponseCode;
import com.bincher.getCoupon.dto.ResponseDto;
import com.bincher.getCoupon.dto.request.coupon.PostCouponRequestDto;
import com.bincher.getCoupon.dto.request.coupon.ReceiveCouponRequestDto;
import com.bincher.getCoupon.dto.response.coupon.GetCouponListResponseDto;
import com.bincher.getCoupon.dto.response.coupon.GetCouponResponseDto;
import com.bincher.getCoupon.dto.response.coupon.PostCouponResponseDto;
import com.bincher.getCoupon.dto.response.coupon.ReceiveCoupon2ResponseDto;
import com.bincher.getCoupon.dto.response.coupon.ReceiveCouponResponseDto;
import com.bincher.getCoupon.service.CouponQueueService;
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
    private final CouponQueueService couponQueueService;

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

    @PostMapping("/event2")
    public ResponseEntity<? super ReceiveCoupon2ResponseDto> receive2Coupon(
        @RequestBody @Valid ReceiveCouponRequestDto requestBody,
        @AuthenticationPrincipal String id
    ){
        ResponseEntity<? super ReceiveCoupon2ResponseDto> response = couponService.receiveCoupon2(requestBody, id);
        return response;
    }
    
    // 대기열 위치 확인 엔드포인트
    @GetMapping("/queue/{couponId}")
    public ResponseEntity<? super ReceiveCoupon2ResponseDto> getQueuePosition(
            @PathVariable("couponId") int couponId,
            @AuthenticationPrincipal String id
    ) {
        return couponService.getQueuePosition(id, couponId);
    }

    // 관리자용: 대기열 처리 시작 엔드포인트
    @PostMapping("/admin/process-queue/{couponId}")
    public ResponseEntity<ResponseDto> processQueue(
            @PathVariable("couponId") int couponId,
            @AuthenticationPrincipal String id
    ) {
        // 관리자 권한 체크 로직 추가
        couponQueueService.processQueue(couponId);
        return ResponseEntity.ok(new ResponseDto(ResponseCode.SUCCESS, "대기열 처리가 시작되었습니다."));
    }

}
