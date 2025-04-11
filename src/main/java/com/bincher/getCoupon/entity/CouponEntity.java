package com.bincher.getCoupon.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity(name="coupon")
@Table(name="coupon")
@NoArgsConstructor
@AllArgsConstructor
public class CouponEntity {
    
    @Id
    private int id;
    private String name;
    private int amount;
    private String startDate;
    private String endDate;
    private String couponImage;
}