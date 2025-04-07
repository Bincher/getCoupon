package com.bincher.getCoupon.service.implement;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bincher.getCoupon.dto.ResponseDto;
import com.bincher.getCoupon.dto.request.auth.IdCheckRequestDto;
import com.bincher.getCoupon.dto.request.auth.SignUpRequestDto;
import com.bincher.getCoupon.dto.response.auth.IdCheckResponseDto;
import com.bincher.getCoupon.dto.response.auth.SignUpResponseDto;
import com.bincher.getCoupon.entity.UserEntity;
import com.bincher.getCoupon.repository.UserRepository;
import com.bincher.getCoupon.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImplement implements AuthService{
    
    private final UserRepository userRepository;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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

    @Override
    public ResponseEntity<? super SignUpResponseDto> signUp(SignUpRequestDto dto) {
        
        try{
            String id = dto.getId();
            boolean existedId = userRepository.existsById(id);
            if(existedId) return SignUpResponseDto.duplicateId();

            String password = dto.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            dto.setPassword(encodedPassword);

            UserEntity userEntity = new UserEntity(dto);
            userRepository.save(userEntity);
        }catch(Exception exception){
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return SignUpResponseDto.success();
    }
}
