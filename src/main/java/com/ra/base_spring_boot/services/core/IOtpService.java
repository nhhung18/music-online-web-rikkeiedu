package com.ra.base_spring_boot.services.core;

import com.ra.base_spring_boot.dto.req.OtpReq;
import com.ra.base_spring_boot.dto.req.ResendOtpRequest;

public interface IOtpService {
    void verifyOtp(OtpReq otpRequest);

    void resendOtp(ResendOtpRequest email);
}
