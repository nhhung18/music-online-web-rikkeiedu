package com.ra.base_spring_boot.services.core.impl;

import com.ra.base_spring_boot.dto.req.OtpReq;
import com.ra.base_spring_boot.dto.req.ResendOtpRequest;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.OTP;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.UserStatus;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.repository.OtpRepository;
import com.ra.base_spring_boot.services.core.IEmailService;
import com.ra.base_spring_boot.services.core.IOtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements IOtpService {

    private final OtpRepository otpRepository;
    private final IUserRepository userRepository;
    private final IEmailService emailService;

    @Override
    public void verifyOtp(OtpReq otpRequest) {
        OTP otp = findAndValidateOtp(otpRequest);

        User user = activateUserByEmail(otp.getEmail());

        otp.setVerified(true);
        otpRepository.save(otp);
    }

    private OTP findAndValidateOtp(OtpReq otpRequest) {
        OTP otp = otpRepository.findByEmail(otpRequest.getEmail())
                .orElseThrow(() -> new HttpBadRequest("OTP not found"));

        if (otp.getExpireAt().isBefore(LocalDateTime.now())) {
            throw new HttpBadRequest("OTP has expired");
        }

        if (!otp.getOtp().equals(otpRequest.getOtp())) {
            throw new HttpBadRequest("Invalid OTP");
        }

        return otp;
    }

    private User activateUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new HttpNotFound("User not found"));

        user.setStatus(UserStatus.ACTIVE);
        return userRepository.save(user);
    }



    @Override
    @Transactional
    public void resendOtp(ResendOtpRequest request) {
        User user = findAndValidateUser(request.getEmail());

        otpRepository.findByEmail(request.getEmail()).ifPresent(otpRepository::delete);

        emailService.createAndSendOtp(request.getEmail());
    }

    private User findAndValidateUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new HttpNotFound("User not found"));

        if (user.getStatus() != UserStatus.VERIFY) {
            throw new HttpBadRequest("User already verified or blocked");
        }

        return user;
    }

}
