package com.bincher.getCoupon.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity(name="coupon_event")
@Table(name="coupon_event")
@NoArgsConstructor
@AllArgsConstructor
public class CouponEventEntity {
    
    @Id
    private String userId;
    @Id
    private String couponId;
    private String created_date;

}