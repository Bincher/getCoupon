package com.bincher.getCoupon.service.implement;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bincher.getCoupon.dto.ResponseDto;
import com.bincher.getCoupon.dto.request.auth.IdCheckRequestDto;
import com.bincher.getCoupon.dto.response.auth.IdCheckResponseDto;
import com.bincher.getCoupon.repository.UserRepository;
import com.bincher.getCoupon.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImplement implements AuthService{
    
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<? super IdCheckResponseDto> idCheck(IdCheckRequestDto dto){
        
        try{
            String id = dto.getId();
            boolean isExistId = userRepository.existsById(id);
            if(isExistId) return IdCheckResponseDto.duplicateId();
        
        } catch(Exception exception){
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return IdCheckResponseDto.success();
    }
}
