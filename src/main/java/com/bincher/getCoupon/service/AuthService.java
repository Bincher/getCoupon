package com.bincher.getCoupon.service;

import org.springframework.http.ResponseEntity;

import com.bincher.getCoupon.dto.request.auth.IdCheckRequestDto;
import com.bincher.getCoupon.dto.response.auth.IdCheckResponseDto;

public interface AuthService {
    ResponseEntity<? super IdCheckResponseDto> idCheck(IdCheckRequestDto dto);
}