package com.bincher.getCoupon.common;

public interface ResponseMessage {
    
    // HTTP Status 200
    String SUCCESS = "Success";

    // HTTP Status 400
    String VALIDATION_FAILED = "Validation failed";
    String DUPLICATE_ID = "Duplicate id";
    String NOT_EXISTED_USER = "This user does not exist";
    String NOT_EXISTED_COUPON = "This coupon does not exist";
    String INSUFFICIENT_COUPON = "This coupon is insufficient";
    String EXPIRED_COUPON = "This coupon is expired";
    String DUPLICATED_COUPON = "Duplicated Coupon";

    // HTTP Status 401
    String SIGN_IN_FAIL = "Login information mismatch";

    // HTTP Status 403
    String NO_PERMISSION = "Do not have permission";

    // HTTP Status 500
    String DATABASE_ERROR = "Database error";
}
