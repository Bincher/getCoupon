package com.bincher.getCoupon.entity;

import com.bincher.getCoupon.dto.request.auth.SignUpRequestDto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity(name="user")
@Table(name="user")
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    
    @Id
    private String id;
    private String password;
    private String telNumber;
    private String role;

    public UserEntity(SignUpRequestDto dto){
        this.id = dto.getId();
        this.password = dto.getPassword();
        this.telNumber = dto.getTelNumber();
        this.role = "ROLE_USER";
    }

}