package com.bincher.getCoupon.service.implement;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bincher.getCoupon.dto.ResponseDto;
import com.bincher.getCoupon.dto.response.coupon.GetCouponListResponseDto;
import com.bincher.getCoupon.entity.CouponEntity;
import com.bincher.getCoupon.repository.CouponRepository;
import com.bincher.getCoupon.service.CouponService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponServiceImplement implements CouponService{

    private final CouponRepository couponRepository;

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
}
