package com.bincher.getCoupon.dto.response.coupon;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.bincher.getCoupon.common.ResponseCode;
import com.bincher.getCoupon.common.ResponseMessage;
import com.bincher.getCoupon.dto.ResponseDto;

public class ReceiveCouponResponseDto extends ResponseDto{
    
    private ReceiveCouponResponseDto(){
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
    }

    public static ResponseEntity<ReceiveCouponResponseDto> success(){
        ReceiveCouponResponseDto result = new ReceiveCouponResponseDto();
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    public static ResponseEntity<ResponseDto> notExistedUser(){
        ResponseDto result = new ResponseDto(ResponseCode.NOT_EXISTED_USER, ResponseMessage.NOT_EXISTED_USER);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    public static ResponseEntity<ResponseDto> notExistedCoupon(){
        ResponseDto result = new ResponseDto(ResponseCode.NOT_EXISTED_COUPON, ResponseMessage.NOT_EXISTED_COUPON);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    public static ResponseEntity<ResponseDto> insufficientCoupon(){
        ResponseDto result = new ResponseDto(ResponseCode.INSUFFICIENT_COUPON, ResponseMessage.INSUFFICIENT_COUPON);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    public static ResponseEntity<ResponseDto> expiredCoupon(){
        ResponseDto result = new ResponseDto(ResponseCode.EXPIRED_COUPON, ResponseMessage.EXPIRED_COUPON);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    public static ResponseEntity<ResponseDto> duplicatedCoupon(){
        ResponseDto result = new ResponseDto(ResponseCode.DUPLICATED_COUPON, ResponseMessage.DUPLICATED_COUPON);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }
}
