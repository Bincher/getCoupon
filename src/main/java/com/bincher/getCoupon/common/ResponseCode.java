package com.bincher.getCoupon.common;

public interface ResponseCode {

    // HTTP Status 200
    String SUCCESS = "SU";
    String WATTING_QUEUE = "WQ";

    // HTTP Status 400
    String VALIDATION_FAILED = "VF";
    String DUPLICATE_ID = "DI";
    String NOT_EXISTED_USER = "NU";
    String NOT_EXISTED_COUPON = "NC";
    String INSUFFICIENT_COUPON = "IC";
    String EXPIRED_COUPON = "EC";
    String DUPLICATED_COUPON = "DC";

    // HTTP Status 401
    String SIGN_IN_FAIL = "SF";
    
    // HTTP Status 403
    String NO_PERMISSION = "NP";

    // HTTP Status 500
    String DATABASE_ERROR = "DBE";

}