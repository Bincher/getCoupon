package com.bincher.getCoupon.dto.request.coupon;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReceiveCouponRequestDto {
    
    @NotNull
    int couponId;
}
