package com.bincher.getCoupon.dto.response.coupon;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.bincher.getCoupon.common.ResponseCode;
import com.bincher.getCoupon.common.ResponseMessage;
import com.bincher.getCoupon.dto.ResponseDto;

public class PostCouponResponseDto extends ResponseDto{

    private PostCouponResponseDto(){
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
    }

    public static ResponseEntity<PostCouponResponseDto> success(){
        PostCouponResponseDto result = new PostCouponResponseDto();
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    
}
