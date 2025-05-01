package com.bincher.getCoupon.entity;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import com.bincher.getCoupon.dto.request.coupon.ReceiveCouponRequestDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter
@Entity(name="coupon_event")
@Table(name="coupon_event")
@NoArgsConstructor
@AllArgsConstructor
@IdClass(UserCouponId.class)
public class CouponEventEntity {
    
    @Id
    @Column(name = "user_id")
    private String userId;

    @Id
    @Column(name = "coupon_id")
    private int couponId;

    @Column(name = "created_date")
    private String createdDate;

    public CouponEventEntity(ReceiveCouponRequestDto dto, String userId){

        Date now = Date.from(Instant.now());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String createdDatetime = simpleDateFormat.format(now);

        this.userId = userId;
        this.couponId = dto.getCouponId();
        this.createdDate = createdDatetime;
    }

    public CouponEventEntity(int id, String userId){

        Date now = Date.from(Instant.now());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String createdDatetime = simpleDateFormat.format(now);

        this.userId = userId;
        this.couponId = id;
        this.createdDate = createdDatetime;
    }
}