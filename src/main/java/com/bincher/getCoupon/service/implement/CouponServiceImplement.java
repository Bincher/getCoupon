package com.bincher.getCoupon.service.implement;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
    public ResponseEntity<? super GetCouponResponseDto> getCoupon(int couponId) {

        CouponEntity couponEntity = null;
        try {
            couponEntity = couponRepository.findById(couponId);
            if(couponEntity == null) return GetCouponResponseDto.notExistedCoupon();

        } catch(Exception exception){
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return GetCouponResponseDto.success(couponEntity);
    }
}
