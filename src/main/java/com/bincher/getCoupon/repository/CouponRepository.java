package com.bincher.getCoupon.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.bincher.getCoupon.entity.CouponEntity;

import jakarta.persistence.LockModeType;

public interface CouponRepository extends JpaRepository<CouponEntity, Integer> {

    List<CouponEntity> findByOrderByIdDesc();
    CouponEntity findById(int id);

    @Modifying
    @Transactional // 이 메소드 자체도 트랜잭션으로 실행되도록 (선택적이나 권장)
    @Query(value = "UPDATE coupon SET amount = amount - 1 WHERE id = :couponId AND amount > 0", nativeQuery = true)
    int decreaseAmountIfAvailable(@Param("couponId") int couponId);
}
