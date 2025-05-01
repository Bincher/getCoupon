package com.bincher.getCoupon.service;

import org.redisson.api.RBlockingQueue;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bincher.getCoupon.entity.CouponEntity;
import com.bincher.getCoupon.entity.CouponEventEntity;
import com.bincher.getCoupon.entity.UserCouponId;
import com.bincher.getCoupon.repository.CouponEventRepository;
import com.bincher.getCoupon.repository.CouponRepository;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponQueueService {

    private final RBlockingQueue<String> couponQueue;
    private final CouponRepository couponRepository;
    private final CouponEventRepository couponEventRepository;

    private final RedissonClient redissonClient;

    // 대기열에 사용자 추가
    public int addToQueue(String userId, int couponId) {
        // userId:couponId 형태로 큐에 저장
        String queueItem = userId + ":" + couponId;
        couponQueue.add(queueItem);
        
        // 현재 대기 위치 반환
        return getQueuePosition(userId, couponId);
    }

    // 대기열 위치 확인 로직 개선
    public int getQueuePosition(String userId, int couponId) {
        String searchItem = userId + ":" + couponId;
        RList<String> tempList = redissonClient.getList("TEMP_QUEUE");
        
        // 원자적 연산으로 큐 복사
        redissonClient.getLock("QUEUE_LOCK").lock();
        try {
            couponQueue.drainTo(tempList);
            int position = tempList.indexOf(searchItem) + 1;
            couponQueue.addAll(tempList);
            tempList.clear();
            return position;
        } finally {
            redissonClient.getLock("QUEUE_LOCK").unlock();
        }
    }

    // 쿠폰 처리 로직 개선
    // 기존 processQueue 메서드 리팩토링
    @Transactional
    public void processQueue(int couponId) {
        CouponEntity coupon = couponRepository.findById(couponId);
        if (coupon == null || coupon.getAmount() <= 0) return;

        int remainingCoupons = coupon.getAmount();
        
        while (remainingCoupons > 0) {
            String queueItem = couponQueue.poll();
            if (queueItem == null) break;

            boolean success = processCoupon(queueItem, couponId); // 변경된 부분
            if(success) remainingCoupons--;
        }
    }

     // 새로운 처리 메서드 추가
    private boolean processCoupon(String queueItem, int couponId) {
        try {
            String[] parts = queueItem.split(":");
            String userId = parts[0];
            int requestedCouponId = Integer.parseInt(parts[1]);
            
            if (requestedCouponId != couponId) {
                couponQueue.add(queueItem);
                return false;
            }
            
            // 원자적 쿠폰 감소
            int updated = couponRepository.atomicDecrement(couponId);
            if(updated == 0) return false;

            UserCouponId userCouponId = new UserCouponId(userId, couponId);
            if(couponEventRepository.existsById(userCouponId)) return false;

            CouponEventEntity couponEvent = new CouponEventEntity();
            couponEvent.setUserId(userId);
            couponEvent.setCouponId(couponId);
            couponEventRepository.save(couponEvent);
            
            return true;
        } catch (Exception e) {
            couponQueue.add(queueItem); // 재시도를 위해 큐에 다시 추가
            return false;
        }
    }
}