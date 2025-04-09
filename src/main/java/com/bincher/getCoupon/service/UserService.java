package com.bincher.getCoupon.service;

import org.springframework.http.ResponseEntity;

import com.bincher.getCoupon.dto.response.user.GetSignInUserResponseDto;

public interface UserService {
    ResponseEntity<? super GetSignInUserResponseDto> getSignInUser(String id);
}
