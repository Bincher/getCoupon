package com.bincher.getCoupon.dto.response.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.bincher.getCoupon.common.ResponseCode;
import com.bincher.getCoupon.common.ResponseMessage;
import com.bincher.getCoupon.dto.ResponseDto;
import com.bincher.getCoupon.entity.UserEntity;

import lombok.Getter;

@Getter
public class GetSignInUserResponseDto extends ResponseDto{
    
    private String id;
    private String telNumber;
    private String profileImage;
    private String role;

    private GetSignInUserResponseDto(UserEntity userEntity){
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
        this.id = userEntity.getId();
        this.telNumber = userEntity.getTelNumber();
        this.profileImage = userEntity.getTelNumber();
        this.role = userEntity.getRole();
    }

    public static ResponseEntity<GetSignInUserResponseDto> success(UserEntity userEntity){
        GetSignInUserResponseDto result = new GetSignInUserResponseDto(userEntity);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    public static ResponseEntity<ResponseDto> notExistUser(){
        ResponseDto result = new ResponseDto(ResponseCode.NOT_EXISTED_USER, ResponseMessage.NOT_EXISTED_USER);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
    }
}
