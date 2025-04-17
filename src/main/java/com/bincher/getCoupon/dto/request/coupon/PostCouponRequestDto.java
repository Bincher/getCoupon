package com.bincher.getCoupon.dto.request.coupon;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostCouponRequestDto {
    
    @NotBlank
    private String name;

    @NotNull
    private int amount;

    @NotBlank
    private String startDate;

    @NotBlank
    private String endDate;

    private String couponImage;
}
