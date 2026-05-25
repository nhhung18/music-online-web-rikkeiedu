package com.ra.base_spring_boot.services.core.impl;

import com.ra.base_spring_boot.dto.req.ChangePassword;
import com.ra.base_spring_boot.dto.req.UpdatePassword;
import com.ra.base_spring_boot.dto.req.UserUpdate;
import com.ra.base_spring_boot.dto.resp.*;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.RoleName;
import com.ra.base_spring_boot.model.constants.UserStatus;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.repository.OtpRepository;
import com.ra.base_spring_boot.services.core.IEmailService;
import com.ra.base_spring_boot.services.core.IUserService;
import com.ra.base_spring_boot.services.external.cloudinary.CloudinaryService;
import com.ra.base_spring_boot.util.FileUploadUtil;
import com.ra.base_spring_boot.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;


@Service("defaultUserService")
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpRepository otpRepository;
    private final IEmailService emailService;
    private final ModelMapper modelMapper;

    @Autowired
    private final CloudinaryService cloudinaryService;

    @Override
    public Page<UserResp> getAllUser(Pageable pageable, String name) {
        Page<User> userList = null;
        if (name != null && !name.isBlank()) {
            userList = userRepository.findByLastNameContainingIgnoreCase(pageable, name);
            if (userList.isEmpty()) {
                userList = userRepository.findByFirstNameContainingIgnoreCase(pageable, name);
            }
        } else {
            userList = userRepository.findAll(pageable);
        }
        return userList.map(user -> modelMapper.map(user, UserResp.class));
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public ArtistResp getArtistById(Long artistId) {
        User artist = userRepository.findById(artistId)
                .orElseThrow(() -> new HttpNotFound("Artist not found"));

        boolean isArtist = artist.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equals(RoleName.ROLE_ARTIST));

        if (!isArtist) {
            throw new HttpBadRequest("User is not an artist");
        }

        return ArtistResp.fromUser(artist);
    }


    @Override
    public User updateUser(UserUpdate newUser) {
        Long id = SecurityUtils.getCurrentUserId();

        User user = userRepository.findById(id)
                .orElseThrow(() -> new HttpNotFound("User not found"));
        validateAndUpdate(newUser, user);
        return userRepository.save(user);
    }

    private void validateAndUpdate(UserUpdate newUser, User user){
        if (newUser.getFirstName() != null && !newUser.getFirstName().trim().isEmpty()) {
            user.setFirstName(newUser.getFirstName());
        }

        if (newUser.getLastName() != null && !newUser.getLastName().trim().isEmpty()) {
            user.setLastName(newUser.getLastName());
        }

        if (newUser.getProfileImage() != null && !newUser.getProfileImage().trim().isEmpty()) {
            user.setProfileImage(newUser.getProfileImage());
        }

        if (newUser.getBio() != null && !newUser.getBio().trim().isEmpty()) {
            user.setBio(newUser.getBio());
        }
    }

    @Override
    public void changePassword(ChangePassword request) {
        Long id = SecurityUtils.getCurrentUserId();

        User user = userRepository.findById(id)
                .orElseThrow(() -> new HttpNotFound("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new HttpBadRequest("Old password is incorrect");
        }
        if (!Objects.equals(request.getNewPassword(), request.getConfirmPassword())) {
            throw new HttpBadRequest("Passwords don't match");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public void forgotPassword(String email) {
        otpRepository.findByEmail(email).ifPresent(otpRepository::delete);

        emailService.createAndSendOtp(email);
    }

    @Override
    public void resetPassword(UpdatePassword request) {

        if (otpRepository.existsByEmail(request.getEmail())) {
            throw new HttpNotFound("OTP not verified");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new HttpNotFound("User not found for email: " + request.getEmail()));


        if (!Objects.equals(request.getNewPassword(), request.getConfirmPassword())) {
            throw new HttpBadRequest("Passwords don't match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        otpRepository.deleteByEmail(request.getEmail());
    }

    public void changeUserStatus(Long userid, String userStatus) {
        User user = userRepository.findById(userid)
                .orElseThrow(() -> new HttpNotFound("User not found"));

        try {
            UserStatus statusEnum = UserStatus.valueOf(userStatus.toUpperCase());
            user.setStatus(statusEnum);
        } catch (IllegalArgumentException e) {
            throw new HttpBadRequest("Invalid status: " + userStatus);
        }
        userRepository.save(user);
    }

    @Override
    public void uploadImage(Long userId, MultipartFile file) throws Exception {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User id does not exist!"));
        FileUploadUtil.assertAllowed(file, FileUploadUtil.IMAGE_PATTERN);
        final String fileName = FileUploadUtil.getFileName(file.getOriginalFilename());
        final CloudinaryResp response = cloudinaryService.uploadProfile(file, fileName);
        user.setProfileImage(response.getUrl());
        userRepository.save(user);
    }

    @Override
    public Page<ArtistResp> getAllArtists(Pageable pageable, String name) {
        Page<User> artistList = null;
        if (name != null && !name.isBlank()) {
            artistList = userRepository.findByArtistName(pageable, name);
        } else {
            artistList = userRepository.findAllArtists(pageable);
        }
        return artistList.map(ArtistResp::fromUser);
    }

    @Override
    public List<ArtistResp> getFeaturedArtists() {
        Pageable pageable = PageRequest.of(0, 10);
        List<User> artists = userRepository.findFeaturedArtists(pageable);
        return artists.stream()
                .map(ArtistResp::fromUser)
                .toList();
    }

    @Override
    public Page<StatusUserResp> getUserStats(Pageable pageable, String status) {
        Page<User> statusUserRespList = Page.empty();
        if (status.equals("active")) {
            statusUserRespList = userRepository.findAllByStatusActive(pageable);
        } else if (status.equals("blocked")) {
            statusUserRespList = userRepository.findAllByStatusBlock(pageable);
        } else if (status.equals("verify")) {
            statusUserRespList = userRepository.findAllByStatusVerify(pageable);
        }
        return statusUserRespList.map(a -> modelMapper.map(a, StatusUserResp.class));
    }

    @Override
    public Long countUserStatus(String status) {
        if (status.equals("active")) {
            return userRepository.countActiveUser();
        } else if (status.equals("blocked")) {
            return userRepository.countBlockUser();
        }
        return userRepository.countVerifyUser();
    }

    public User getMyInfo() {
        return SecurityUtils.getCurrentUser();
    }

    ;
}

