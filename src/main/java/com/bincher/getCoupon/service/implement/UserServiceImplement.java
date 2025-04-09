package com.bincher.getCoupon.service.implement;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bincher.getCoupon.dto.ResponseDto;
import com.bincher.getCoupon.dto.response.user.GetSignInUserResponseDto;
import com.bincher.getCoupon.entity.UserEntity;
import com.bincher.getCoupon.repository.UserRepository;
import com.bincher.getCoupon.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImplement implements UserService{
    
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<? super GetSignInUserResponseDto> getSignInUser(String id){
        
        UserEntity userEntity = null;

        try{
            userEntity = userRepository.findById(id);
            if(userEntity == null) return GetSignInUserResponseDto.notExistUser();
            
        }catch(Exception exception){
            exception.printStackTrace();
            return ResponseDto.databaseError();
        }

        return GetSignInUserResponseDto.success(userEntity);
    }
}
