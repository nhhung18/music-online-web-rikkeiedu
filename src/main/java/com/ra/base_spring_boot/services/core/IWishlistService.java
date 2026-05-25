package com.ra.base_spring_boot.services.core;

import com.ra.base_spring_boot.dto.resp.SongResp;

import java.util.Set;

public interface IWishlistService {
    Set<SongResp> getWishlist(Long userId);
    SongResp addToWishlist(Long userId, Long songId);
    void removeFromWishlist(Long userId, Long songId);
}

