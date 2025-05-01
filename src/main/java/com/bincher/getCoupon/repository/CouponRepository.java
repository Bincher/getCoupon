package com.bincher.getCoupon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bincher.getCoupon.entity.CouponEntity;

public interface CouponRepository extends JpaRepository<CouponEntity, Integer> {

    List<CouponEntity> findByOrderByIdDesc();
    CouponEntity findById(int id);
    List<CouponEntity> findByAmountGreaterThan(int i);

    @Modifying
    @Query(
        value = "UPDATE coupon SET amount = amount - 1 WHERE id = :id AND amount > 0",
        nativeQuery = true // (4) 네이티브 쿼리 사용
    )
    int atomicDecrement(@Param("id") int id);
}
