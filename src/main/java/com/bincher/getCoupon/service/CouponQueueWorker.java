package com.bincher.getCoupon.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bincher.getCoupon.entity.CouponEntity;
import com.bincher.getCoupon.entity.CouponEventEntity;
import com.bincher.getCoupon.repository.CouponEventRepository;
import com.bincher.getCoupon.repository.CouponRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponQueueWorker {

    private final RedissonClient redissonClient;
    private final CouponRepository couponRepository;
    private final CouponEventRepository couponEventRepository;

    private final List<Integer> processingCouponIds = List.of(1);

    @Scheduled(fixedDelay = 100) // 처리 간격 조절 가능
    public void processCouponQueues() {
        for (int couponId : processingCouponIds) {
            processQueueForCoupon(couponId);
        }
    }

    private void processQueueForCoupon(int couponId) {
        String queueKey = "coupon_queue:" + couponId;
        RList<String> queue = redissonClient.getList(queueKey);

        // 한 번에 처리할 개수 제한 (선택적, 부하 조절)
        int batchSize = 10;
        int processedCount = 0;

        while (!queue.isEmpty() && processedCount < batchSize) {
            String userId = queue.remove(0); // FIFO
            if (userId == null) continue; // 혹시 모를 null 체크

            String resultKey = "coupon_result:" + couponId + ":" + userId;
            RBucket<String> resultBucket = redissonClient.getBucket(resultKey);

            // 이미 처리된 요청인지 확인 (선택적, 성능 향상)
            if (resultBucket.isExists() && !"QUEUED".equals(resultBucket.get())) {
                 processedCount++;
                 continue;
            }

            try {
                // 트랜잭션으로 발급 시도
                boolean issued = tryIssueCouponTransactional(userId, couponId);
                if (issued) {
                    resultBucket.set("SUCCESS");
                    log.info("쿠폰 발급 성공: couponId={}, userId={}", couponId, userId);
                } else {
                    // 재고 부족이 확실한 경우
                    resultBucket.set("INSUFFICIENT");
                    log.info("재고 부족: couponId={}, userId={}", couponId, userId);
                }
            } catch (DuplicateKeyException e) {
                // 중복 발급 시도
                resultBucket.set("DUPLICATE");
                log.info("중복 발급 시도: couponId={}, userId={}", couponId, userId);
            } catch (Exception e) {
                // 그 외 예외 (DB 오류 등)
                resultBucket.set("FAIL");
                log.error("쿠폰 발급 실패: couponId={}, userId={}, error={}", couponId, userId, e.getMessage());
                // 실패 시 재시도 로직 (큐에 다시 넣기 등) - 주의해서 구현
                // queue.add(userId);
            } finally {
                 processedCount++;
            }
        }
    }

    // 트랜잭션 처리 메소드
    @Transactional // 이 어노테이션이 매우 중요!
    public boolean tryIssueCouponTransactional(String userId, int couponId) {
        // 1. Atomic Update로 재고 감소 시도
        int updated = couponRepository.decreaseAmountIfAvailable(couponId);
        if (updated == 0) {
            return false; // 업데이트된 row가 0이면 재고가 없거나 이미 0이었음
        }

        // 2. 발급 내역 저장 (PK 중복 시 DuplicateKeyException 발생)
        CouponEventEntity event = new CouponEventEntity();
        event.setUserId(userId);
        event.setCouponId(couponId);
        couponEventRepository.save(event); // saveAndFlush 대신 save 사용

        // 여기까지 성공하면 트랜잭션 커밋
        return true;
    }

    // tryIssueCouponWithPessimisticLock 메소드는 삭제
}