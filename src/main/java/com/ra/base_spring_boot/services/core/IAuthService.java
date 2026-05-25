package com.ra.base_spring_boot.services.core;

import com.ra.base_spring_boot.dto.req.FormLogin;
import com.ra.base_spring_boot.dto.req.FormRegister;
import com.ra.base_spring_boot.dto.req.LogoutReq;
import com.ra.base_spring_boot.dto.resp.AccessTokenResp;
import com.ra.base_spring_boot.dto.resp.JwtResp;
import com.ra.base_spring_boot.dto.req.*;
import org.springframework.web.multipart.MultipartFile;

public interface IAuthService
{

    void register(FormRegister formRegister, MultipartFile file) throws Exception;

    JwtResp login(FormLogin formLogin);

    void logout(RefreshReq request, LogoutReq req);

    AccessTokenResp refreshToken(RefreshReq request);

}
