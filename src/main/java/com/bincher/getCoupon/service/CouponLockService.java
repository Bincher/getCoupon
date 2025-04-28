package com.bincher.getCoupon.service;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponLockService {

    private final RedissonClient redissonClient;

    public boolean tryAcquireLock(int couponId) {
        RLock lock = redissonClient.getLock("COUPON_LOCK:" + couponId);
        try {
            // 1초 대기, 3초 후 자동 해제
            return lock.tryLock(1, 3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public void releaseLock(int couponId) {
        RLock lock = redissonClient.getLock("COUPON_LOCK:" + couponId);
        if (lock.isLocked() && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}