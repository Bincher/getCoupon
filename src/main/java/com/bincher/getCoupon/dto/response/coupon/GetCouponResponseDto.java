package com.bincher.getCoupon.dto.response.coupon;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.bincher.getCoupon.common.ResponseCode;
import com.bincher.getCoupon.common.ResponseMessage;
import com.bincher.getCoupon.dto.ResponseDto;
import com.bincher.getCoupon.entity.CouponEntity;

import lombok.Getter;

@Getter
public class GetCouponResponseDto extends ResponseDto{

    private int id;
    private String name;
    private int amount;
    private String startDate;
    private String endDate;
    private String couponImage;
    
    private GetCouponResponseDto(CouponEntity couponEntity){
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
        this.id = couponEntity.getId();
        this.name = couponEntity.getName();
        this.amount = couponEntity.getAmount();
        this.startDate = couponEntity.getStartDate();
        this.endDate = couponEntity.getEndDate();
        this.couponImage = couponEntity.getCouponImage();
    }

    public static ResponseEntity<GetCouponResponseDto> success(CouponEntity couponEntity){
        GetCouponResponseDto result = new GetCouponResponseDto(couponEntity);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    public static ResponseEntity<ResponseDto> notExistedCoupon(){
        ResponseDto result = new ResponseDto(ResponseCode.NOT_EXISTED_COUPON, ResponseMessage.NOT_EXISTED_COUPON);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }
}
