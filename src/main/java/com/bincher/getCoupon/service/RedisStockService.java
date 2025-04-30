package com.bincher.getCoupon.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisStockService {
    private final RedisTemplate<String, String> redisTemplate;

    // 재고 초기화 (쿠폰 생성 시 호출)
    public void initializeStock(int couponId, int stock) {
        redisTemplate.opsForValue().set("coupon:stock:" + couponId, String.valueOf(stock));
    }

    // 재고 감소 (원자적 연산)
    public Long decreaseStock(int couponId) {
        return redisTemplate.opsForValue().decrement("coupon:stock:" + couponId);
    }

    
}