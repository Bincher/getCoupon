package com.bincher.getCoupon.service.implement;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bincher.getCoupon.dto.ResponseDto;
import com.bincher.getCoupon.dto.request.coupon.PostCouponRequestDto;
import com.bincher.getCoupon.dto.request.coupon.ReceiveCouponRequestDto;
import com.bincher.getCoupon.dto.response.coupon.GetCouponListResponseDto;
import com.bincher.getCoupon.dto.response.coupon.GetCouponResponseDto;
import com.bincher.getCoupon.dto.response.coupon.PostCouponResponseDto;
import com.bincher.getCoupon.dto.response.coupon.ReceiveCoupon2ResponseDto;
import com.bincher.getCoupon.dto.response.coupon.ReceiveCouponResponseDto;
import com.bincher.getCoupon.entity.CouponEntity;
import com.bincher.getCoupon.entity.CouponEventEntity;
import com.bincher.getCoupon.entity.UserCouponId;
import com.bincher.getCoupon.entity.UserEntity;
import com.bincher.getCoupon.repository.CouponEventRepository;
import com.bincher.getCoupon.repository.CouponRepository;
import com.bincher.getCoupon.repository.UserRepository;
import com.bincher.getCoupon.service.CouponQueueService;
import com.bincher.getCoupon.service.CouponService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponServiceImplement implements CouponService{

    private final CouponRepository couponRepository;
    private final CouponEventRepository couponEventRepository;
    private final UserRepository userRepository;

    private final RedissonClient redissonClient;
    private final CouponQueueService couponQueueService;

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

            RAtomicLong couponCounter = redissonClient.getAtomicLong("COUPON_COUNTER_" + couponEntity.getId());
            couponCounter.set(dto.getAmount());

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

    @Override
    public ResponseEntity<? super ReceiveCoupon2ResponseDto> receiveCoupon2(ReceiveCouponRequestDto dto, String userId) {
        UserEntity userEntity = null;
        CouponEntity couponEntity = null;

        try {
            userEntity = userRepository.findById(userId);
            if (userEntity == null) return ReceiveCouponResponseDto.notExistedUser();

            int couponId = dto.getCouponId();
            couponEntity = couponRepository.findById(couponId);
            if (couponEntity == null) return ReceiveCouponResponseDto.notExistedCoupon();

            Date now = Date.from(Instant.now());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date endDate = simpleDateFormat.parse(couponEntity.getEndDate());
            
            if (endDate.before(now)) return ReceiveCouponResponseDto.expiredCoupon();

            // 대기열에 사용자 추가
            int position = couponQueueService.addToQueue(userId, couponId);
            
            return ReceiveCoupon2ResponseDto.queued(position);

        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

    // 새로운 메서드: 대기열 위치 조회
    public ResponseEntity<? super ReceiveCoupon2ResponseDto> getQueuePosition(String userId, int couponId) {
        try {
            UserEntity userEntity = userRepository.findById(userId);
            if (userEntity == null) return ReceiveCouponResponseDto.notExistedUser();
            
            int position = couponQueueService.getQueuePosition(userId, couponId);
            if (position == -1) {
                return ReceiveCoupon2ResponseDto.notInQueue();
            }
            
            return ReceiveCoupon2ResponseDto.success(position);
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }
    }
    
}
