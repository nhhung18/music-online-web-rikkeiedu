package com.ra.base_spring_boot.services.core;

import com.ra.base_spring_boot.model.UserFavorSong;

import java.util.List;

public interface IUserFavorSongService {

    UserFavorSong addFavorSong(Long userId, Long songId) throws Exception;

    List<UserFavorSong> getFavorSong();

    void deleteFavorById(Long userId, Long songId) throws Exception;
}
