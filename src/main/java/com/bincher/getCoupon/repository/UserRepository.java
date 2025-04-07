package com.bincher.getCoupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bincher.getCoupon.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    boolean existsById(String id);
    UserEntity findById(String id);
}