package com.bincher.getCoupon.service.implement;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.bincher.getCoupon.service.CouponLockService;
import com.bincher.getCoupon.service.CouponService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponServiceImplement implements CouponService{

    private final CouponLockService couponLockService;
    private final CouponRepository couponRepository;
    private final CouponEventRepository couponEventRepository;
    private final UserRepository userRepository;

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

        CouponEntity couponEntity = null;
        int couponId = dto.getCouponId();
        couponEntity = couponRepository.findById(couponId);
        
        try {
            
            if(couponEntity == null) return ReceiveCouponResponseDto.notExistedCoupon();
            if (!couponLockService.tryAcquireLock(couponId)) {
                throw new RuntimeException("쿠폰 발급 중입니다. 잠시 후 다시 시도해주세요.");
            }    

            CouponEntity coupon = couponRepository.findById(couponId);

            if (coupon.getAmount() <= 0) {
                throw new RuntimeException("쿠폰이 모두 소진되었습니다.");
            }

            coupon.decreaseAmount();
            couponRepository.save(coupon);

            CouponEventEntity couponEventEntity = new CouponEventEntity(couponId, userId);
            couponEventRepository.save(couponEventEntity);
        } finally {
            couponLockService.releaseLock(couponId);
        }

        return ReceiveCouponResponseDto.success();
    }
}
