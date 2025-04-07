package com.bincher.getCoupon.service;

import org.springframework.http.ResponseEntity;

import com.bincher.getCoupon.dto.request.auth.IdCheckRequestDto;
import com.bincher.getCoupon.dto.request.auth.SignUpRequestDto;
import com.bincher.getCoupon.dto.response.auth.IdCheckResponseDto;
import com.bincher.getCoupon.dto.response.auth.SignUpResponseDto;

public interface AuthService {
    ResponseEntity<? super IdCheckResponseDto> idCheck(IdCheckRequestDto dto);
    ResponseEntity<? super SignUpResponseDto> signUp(SignUpRequestDto dto);
}