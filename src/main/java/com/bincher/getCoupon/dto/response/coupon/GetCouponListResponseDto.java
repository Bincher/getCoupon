package com.bincher.getCoupon.dto.response.coupon;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.bincher.getCoupon.common.ResponseCode;
import com.bincher.getCoupon.common.ResponseMessage;
import com.bincher.getCoupon.dto.ResponseDto;
import com.bincher.getCoupon.dto.object.CouponListItem;
import com.bincher.getCoupon.entity.CouponEntity;

import lombok.Getter;

@Getter
public class GetCouponListResponseDto extends ResponseDto{
    
    private List<CouponListItem> couponList;

    private GetCouponListResponseDto(List<CouponEntity> couponEntities){
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
        this.couponList = CouponListItem.getList(couponEntities);
    }

    public static ResponseEntity<GetCouponListResponseDto> success(List<CouponEntity> couponEntities){
        GetCouponListResponseDto result = new GetCouponListResponseDto(couponEntities);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
