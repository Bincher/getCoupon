package com.bincher.getCoupon.service.implement;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.bincher.getCoupon.dto.ResponseDto;
import com.bincher.getCoupon.dto.request.coupon.PostCouponRequestDto;
import com.bincher.getCoupon.dto.request.coupon.ReceiveCouponRequestDto;
import com.bincher.getCoupon.dto.response.coupon.GetCouponListResponseDto;
import com.bincher.getCoupon.dto.response.coupon.GetCouponResponseDto;
import com.bincher.getCoupon.dto.response.coupon.PostCouponResponseDto;
import com.bincher.getCoupon.dto.response.coupon.ReceiveCouponResponseDto;
import com.bincher.getCoupon.entity.CouponEntity;
import com.bincher.getCoupon.entity.CouponEventEntity;
import com.bincher.getCoupon.entity.UserCouponId;
import com.bincher.getCoupon.entity.UserEntity;
import com.bincher.getCoupon.repository.CouponEventRepository;
import com.bincher.getCoupon.repository.CouponRepository;
import com.bincher.getCoupon.repository.UserRepository;
import com.bincher.getCoupon.service.CouponService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponServiceImplement implements CouponService{

    private final CouponRepository couponRepository;
    private final CouponEventRepository couponEventRepository;
    private final UserRepository userRepository;

    private final RedissonClient redissonClient;

    @Override
    public ResponseEntity<? super GetCouponListResponseDto> getCouponList() {
        
        List<CouponEntity> couponEntities = new ArrayList<>();

        try{
            couponEntities =  couponRepository.findByOrderByIdDesc();
            

        }catch(Exception exception){
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return GetCouponListResponseDto.success(couponEntities);
    }

    @Override
    public ResponseEntity<? super PostCouponResponseDto> postCoupon(PostCouponRequestDto dto) {
        try{


            CouponEntity couponEntity = new CouponEntity(dto);
            couponRepository.save(couponEntity);

        }catch(Exception exception){
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return PostCouponResponseDto.success();
    }

    @Override
    public ResponseEntity<? super ReceiveCouponResponseDto> receiveCoupon(ReceiveCouponRequestDto dto, String userId) {
        
        UserEntity userEntity = null;
        CouponEntity couponEntity = null;

        Date now = Date.from(Instant.now());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try{
            userEntity = userRepository.findById(userId);
            if(userEntity == null) return ReceiveCouponResponseDto.notExistedUser();

            int couponId = dto.getCouponId();
            couponEntity = couponRepository.findById(couponId);
            if(couponEntity == null) return ReceiveCouponResponseDto.notExistedCoupon();

            if(couponEntity.getAmount() < 1) return ReceiveCouponResponseDto.insufficientCoupon();

            Date endDate = simpleDateFormat.parse(couponEntity.getEndDate());
            if (endDate.before(now)) return ReceiveCouponResponseDto.expiredCoupon();

            UserCouponId userCouponId = new UserCouponId(userId, couponId);
            boolean isDuplicated = couponEventRepository.existsById(userCouponId);
            if(isDuplicated) return ReceiveCouponResponseDto.duplicatedCoupon();
            
            couponEntity.decreaseAmount();

            CouponEventEntity couponEventEntity = new CouponEventEntity(dto, userId);
            couponEventRepository.save(couponEventEntity);

        }catch(Exception exception){
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return ReceiveCouponResponseDto.success();
    }

    @Override
    public ResponseEntity<? super GetCouponResponseDto> getCoupon(String couponId) {

        CouponEntity couponEntity = null;
        try {
            int id = Integer.parseInt(couponId);

            couponEntity = couponRepository.findById(id);
            if(couponEntity == null) return GetCouponResponseDto.notExistedCoupon();

        } catch(Exception exception){
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return GetCouponResponseDto.success(couponEntity);
    }

    @Transactional
    public ResponseEntity<? super ReceiveCouponResponseDto> issueCoupon(ReceiveCouponRequestDto dto, String userId) {
        RLock lock = redissonClient.getLock("COUPON_LOCK:" + dto.getCouponId());
        boolean isLocked = false;
        try {
            // 1. 락 획득 (10초 대기, 10초 유지)
            isLocked = lock.tryLock(10, 10, TimeUnit.SECONDS);
            if (!isLocked) {
                return ReceiveCouponResponseDto.duplicatedCoupon();
            }

            // 2. 유저 및 쿠폰 존재 여부 확인
            UserEntity userEntity = userRepository.findById(userId);
            if (userEntity == null) return ReceiveCouponResponseDto.notExistedUser();

            CouponEntity couponEntity = couponRepository.findById(dto.getCouponId());
            if (couponEntity == null) return ReceiveCouponResponseDto.notExistedCoupon();

            // 3. 유효기간 검증
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date endDate = simpleDateFormat.parse(couponEntity.getEndDate());
            if (endDate.before(new Date())) {
                return ReceiveCouponResponseDto.expiredCoupon();
            }

            // 4. 중복 발급 방지
            UserCouponId userCouponId = new UserCouponId(userId, dto.getCouponId());
            if (couponEventRepository.existsById(userCouponId)) {
                return ReceiveCouponResponseDto.duplicatedCoupon();
            }

            // 5. 재고 감소 및 이력 저장
            couponEntity.decreaseAmount();
            couponRepository.save(couponEntity);
            couponEventRepository.save(new CouponEventEntity(dto.getCouponId(), userId));

            // 6. 트랜잭션 완료 후 락 해제
            TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCompletion(int status) {
                        if (lock.isHeldByCurrentThread()) {
                            lock.unlock();
                        }
                    }
                }
            );

            return ReceiveCouponResponseDto.success();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseDto.databaseError();
        } catch (Exception e) {
            // 트랜잭션 롤백 시 락 해제
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
            return ResponseDto.databaseError();
        }
    }

}
