package com.bincher.getCoupon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bincher.getCoupon.entity.CouponEntity;

public interface CouponRepository extends JpaRepository<CouponEntity, Integer> {

    List<CouponEntity> findByOrderByIdDesc();
    CouponEntity findById(int id);
}
