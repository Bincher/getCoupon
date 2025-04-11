package com.bincher.getCoupon.dto.object;

import java.util.ArrayList;
import java.util.List;

import com.bincher.getCoupon.entity.CouponEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CouponListItem {

    private int id;
    private String name;
    private int amount;
    private String startDate;
    private String endDate;
    private String couponImage;

    public CouponListItem(CouponEntity couponEntity){
        this.id = couponEntity.getId();
        this.name = couponEntity.getName();
        this.amount = couponEntity.getAmount();
        this.startDate = couponEntity.getStartDate();
        this.endDate = couponEntity.getEndDate();
        this.couponImage = couponEntity.getCouponImage();
    }

    public static List<CouponListItem> getList(List<CouponEntity> couponEntities){
        List<CouponListItem> list = new ArrayList<>();

        for(CouponEntity couponEntity: couponEntities){
            CouponListItem couponListItem = new CouponListItem(couponEntity);
            list.add(couponListItem);
        }

        return list;
    }
}
