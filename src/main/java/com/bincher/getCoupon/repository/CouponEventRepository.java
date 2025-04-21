package com.bincher.getCoupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bincher.getCoupon.entity.CouponEventEntity;
import com.bincher.getCoupon.entity.UserCouponId;

public interface CouponEventRepository extends JpaRepository<CouponEventEntity, UserCouponId>{
    
}
