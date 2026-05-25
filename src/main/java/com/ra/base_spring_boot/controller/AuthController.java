package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.FormLogin;
import com.ra.base_spring_boot.dto.req.FormRegister;
import com.ra.base_spring_boot.dto.req.LogoutReq;
import com.ra.base_spring_boot.dto.req.RefreshReq;
import com.ra.base_spring_boot.dto.resp.AccessTokenResp;
import com.ra.base_spring_boot.dto.resp.JwtResp;
import com.ra.base_spring_boot.dto.resp.LoginResp;
import com.ra.base_spring_boot.dto.resp.UserResp;
import com.ra.base_spring_boot.services.core.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.time.Duration;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController
{
    private final IAuthService authService;
    private final ModelMapper modelMapper;


    @PostMapping("/login")
    public ResponseEntity<?> handleLogin(@Valid @RequestBody FormLogin formLogin)
    {
        JwtResp jwt = authService.login(formLogin);

        // Set HttpOnly cookie cho refreshToken
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", jwt.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofDays(7))
                .build();

        LoginResp body = LoginResp.builder()
                .accessToken(jwt.getAccessToken())
                .user(modelMapper.map(jwt.getUser(), UserResp.class))
                .roles(jwt.getRoles())
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(body)
                        .build()
        );
    }


    @PostMapping("/register")
    public ResponseEntity<?> handleRegister(@Valid @RequestBody FormRegister formRegister, @RequestPart(value = "file", required = false) MultipartFile file) throws  Exception
    {
        authService.register(formRegister, file);
        return ResponseEntity.created(URI.create("api/v1/auth/register")).body(
                ResponseWrapper.builder()
                        .status(HttpStatus.CREATED)
                        .code(201)
                        .data("Register successfully. Please check your email for OTP.")
                        .build()
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(value = "refreshToken", required = false) String refreshToken,@RequestBody LogoutReq req ){
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ResponseWrapper.builder()
                            .status(HttpStatus.UNAUTHORIZED)
                            .code(401)
                            .data("Refresh token cookie is missing")
                            .build()
            );
        }

        authService.logout(RefreshReq.builder().refreshToken(refreshToken).build(), req);

        // Xóa cookie refreshToken ở trình duyệt
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body(
                        ResponseWrapper.builder()
                                .status(HttpStatus.OK)
                                .code(200)
                                .data("Logout successfully.")
                                .build()
                );
    }


    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(value = "refreshToken", required = false) String refreshToken){
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ResponseWrapper.builder()
                            .status(HttpStatus.UNAUTHORIZED)
                            .code(401)
                            .data("Refresh token cookie is missing")
                            .build()
            );
        }
        AccessTokenResp data = authService.refreshToken(
                RefreshReq.builder().refreshToken(refreshToken).build()
        );
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(data)
                        .build()
        );
    }


}
