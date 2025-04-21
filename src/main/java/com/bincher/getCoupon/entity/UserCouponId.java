package com.bincher.getCoupon.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode
@Data
public class UserCouponId implements Serializable{

    @Column(name = "user_id")
    private String userId;
    @Column(name = "coupon_id")
    private int couponId;

    public UserCouponId(String userId, int couponId) {
        this.userId = userId;
        this.couponId = couponId;
    }
}
