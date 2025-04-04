package com.bincher.getCoupon.common;

public interface ResponseMessage {
    
    // HTTP Status 200
    String SUCCESS = "Success";

    // HTTP Status 400
    String VALIDATION_FAILED = "Validation failed";
    String DUPLICATE_ID = "Duplicate id";

    // HTTP Status 401
    String SIGN_IN_FAIL = "Login information mismatch";

    // HTTP Status 403
    String NO_PERMISSION = "Do not have permission";

    // HTTP Status 500
    String DATABASE_ERROR = "Database error";
}
