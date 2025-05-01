package com.bincher.getCoupon.entity;

import com.bincher.getCoupon.dto.request.coupon.PostCouponRequestDto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
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

    public CouponEntity(PostCouponRequestDto dto){
        this.name = dto.getName();
        this.amount = dto.getAmount();
        this.startDate = dto.getStartDate();
        this.endDate = dto.getEndDate();
        this.couponImage = dto.getCouponImage();
    }

    public void decreaseAmount(){
        this.amount--;
    }
}