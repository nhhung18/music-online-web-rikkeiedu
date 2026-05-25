package com.ra.base_spring_boot.services.core;

import com.ra.base_spring_boot.dto.req.ChangePassword;
import com.ra.base_spring_boot.dto.req.UpdatePassword;
import com.ra.base_spring_boot.dto.req.UserUpdate;
import com.ra.base_spring_boot.dto.resp.ArtistResp;
import com.ra.base_spring_boot.dto.resp.StatusUserResp;
import com.ra.base_spring_boot.dto.resp.UserResp;
import com.ra.base_spring_boot.model.User;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface IUserService {
    Page<UserResp> getAllUser(Pageable pageable, String name);

    User getUserById(Long userId);

    ArtistResp getArtistById(Long artistId);

    User updateUser(UserUpdate user);

    void changePassword(ChangePassword password);

    void forgotPassword(String email);

    void resetPassword(UpdatePassword password);

    void changeUserStatus(Long userId, String userStatus);

    User getMyInfo();

    void uploadImage(Long userId, MultipartFile file) throws Exception;

    // Artist methods
    Page<ArtistResp> getAllArtists(Pageable pageable, String name);

    List<ArtistResp> getFeaturedArtists();

    Page<StatusUserResp> getUserStats(Pageable pageable, String email);

    Long countUserStatus(String status);

}
