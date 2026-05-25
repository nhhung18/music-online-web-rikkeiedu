package com.ra.base_spring_boot.services.core.impl;

import com.ra.base_spring_boot.dto.req.FormLogin;
import com.ra.base_spring_boot.dto.req.FormRegister;
import com.ra.base_spring_boot.dto.req.LogoutReq;
import com.ra.base_spring_boot.dto.resp.AccessTokenResp;
import com.ra.base_spring_boot.dto.resp.JwtResp;
import com.ra.base_spring_boot.dto.req.RefreshReq;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.model.InvalidatedToken;
import com.ra.base_spring_boot.model.Role;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.RoleName;
import com.ra.base_spring_boot.model.constants.UserStatus;
import com.ra.base_spring_boot.repository.IInvalidatedTokenRepository;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.security.jwt.JwtProvider;
import com.ra.base_spring_boot.security.principle.MyUserDetails;
import com.ra.base_spring_boot.security.principle.MyUserDetailsService;
import com.ra.base_spring_boot.services.core.IAuthService;
import com.ra.base_spring_boot.services.core.IEmailService;
import com.ra.base_spring_boot.services.core.IRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService
{
    private final IRoleService roleService;
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final IEmailService emailService;
    private final MyUserDetailsService userDetailsService;
    private final IInvalidatedTokenRepository invalidatedTokenRepository;
    private final UserServiceImpl userService;


    @Override
    public void register(FormRegister formRegister, MultipartFile file) throws Exception {
        Optional<User> existingUserOpt = userRepository.findByEmail(formRegister.getEmail());

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            if (existingUser.getStatus() == UserStatus.ACTIVE) {
                throw new RuntimeException("User existed");
            } else {
                existingUser.setFirstName(formRegister.getFirstName());
                existingUser.setLastName(formRegister.getLastName());
                existingUser.setPassword(passwordEncoder.encode(formRegister.getPassword()));
                existingUser.setBio(formRegister.getBio());
                userRepository.save(existingUser);

                emailService.createAndSendOtp(existingUser.getEmail());
                return;
            }
        }


        Set<Role> roles = new HashSet<>();
        roles.add(roleService.findByRoleName(RoleName.ROLE_USER));

        User user = User.builder()
                .firstName(formRegister.getFirstName())
                .lastName(formRegister.getLastName())
                .email(formRegister.getEmail())
                .password(passwordEncoder.encode(formRegister.getPassword()))
                .status(UserStatus.VERIFY)
                .bio(formRegister.getBio())
                .roles(roles)
                .build();

        userRepository.save(user);
        emailService.createAndSendOtp(user.getEmail());

        if(file != null){
            userService.uploadImage(user.getId(), file);
        }

    }



    @Override
    public JwtResp login(FormLogin formLogin)
    {
        Authentication authentication = authenticateUser(formLogin);
        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        validateUserStatus(user);

        String accessToken = jwtProvider.generateToken(userDetails.getUsername());
        String refreshToken = jwtProvider.generateRefreshToken(userDetails.getUsername());


        return JwtResp.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userDetails.getUser())
                .roles(userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()))
                .build();
    }

    private void validateUserStatus(User user) {
        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new HttpBadRequest("Your account has been blocked");
        }

        if (user.getStatus() == UserStatus.VERIFY) {
            throw new HttpBadRequest("Please verify your email before logging in");
        }
    }

    private Authentication authenticateUser(FormLogin formLogin) {
        try
        {
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(formLogin.getEmail(), formLogin.getPassword()));
        }
        catch (AuthenticationException e)
        {
            throw new HttpBadRequest("Username or password is incorrect");
        }
    }

    @Override
    public void logout(RefreshReq request, LogoutReq req) {
        String refreshToken = validRefreshTokenType(request.getRefreshToken());
        String accessToken = req.getAccessToken();

        try {
            String username = jwtProvider.extractUsername(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            validateToken(refreshToken,userDetails);
            validateToken(accessToken,userDetails);

            List<InvalidatedToken> invalidatedTokens = new ArrayList<>();

            invalidatedTokens.add(
                    createInvalidatedToken(refreshToken)
            );

            invalidatedTokens.add(
                   createInvalidatedToken(accessToken)
            );

            invalidatedTokenRepository.saveAll(invalidatedTokens);
        } catch (Exception e) {
            throw new RuntimeException("Logout failed: " + e.getMessage());
        }
    }

    private String validRefreshTokenType(String token){
        if (token == null || token.isBlank()) {
            throw new RuntimeException("Refresh token is required");
        }

        if (!jwtProvider.isRefreshToken(token)) {
            throw new RuntimeException("Invalid token type. Refresh token is required");
        }
        return token;
    }

    private void validateToken(String token, UserDetails userDetails) {
        if (!jwtProvider.validateToken(token, userDetails)) {
            throw new HttpBadRequest("Invalid token");
        }
    }

    private InvalidatedToken createInvalidatedToken(String token) {
        return InvalidatedToken.builder()
                .id(jwtProvider.extractJti(token))
                .expiryTime(jwtProvider.extractExpiration(token))
                .build();
    }


    @Override
    public AccessTokenResp refreshToken(RefreshReq request) {
        String token = validRefreshTokenType(request.getRefreshToken());


        String username=jwtProvider.extractUsername(token);
        MyUserDetails userDetails = userDetailsService.loadUserByUsername(username);

        validateToken(token,userDetails);

        String newAccessToken = jwtProvider.generateToken(userDetails.getUsername());

        return AccessTokenResp.builder()
                .accessToken(newAccessToken)
                .build();

    }

}
