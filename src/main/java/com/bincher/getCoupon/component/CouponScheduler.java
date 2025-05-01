package com.bincher.getCoupon.component;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bincher.getCoupon.entity.CouponEntity;
import com.bincher.getCoupon.repository.CouponRepository;
import com.bincher.getCoupon.service.CouponQueueService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CouponScheduler {

    private final CouponQueueService couponQueueService;
    private final CouponRepository couponRepository;

    // 15초마다 실행되어 모든 쿠폰의 대기열 처리
    @Scheduled(fixedDelay = 15000)
    public void processCouponQueues() {
        List<CouponEntity> activeCoupons = couponRepository.findByAmountGreaterThan(0);
        
        for (CouponEntity coupon : activeCoupons) {
            couponQueueService.processQueue(coupon.getId());
        }
    }
}