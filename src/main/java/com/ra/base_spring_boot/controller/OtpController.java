package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.OtpReq;
import com.ra.base_spring_boot.dto.req.ResendOtpRequest;
import com.ra.base_spring_boot.services.core.IOtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/otp")
@RequiredArgsConstructor
public class OtpController {

    private final IOtpService otpService;

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody OtpReq otpRequest) {
        otpService.verifyOtp(otpRequest);
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Account verified successfully.")
                        .build());
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> handleResendOtp(@Valid @RequestBody ResendOtpRequest email) {
        otpService.resendOtp(email);
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("OTP sent successfully")
                        .build());
    }
}
