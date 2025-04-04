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
    private String id;
    private String name;
    private String amount;
    private String start_date;
    private String end_date;
}