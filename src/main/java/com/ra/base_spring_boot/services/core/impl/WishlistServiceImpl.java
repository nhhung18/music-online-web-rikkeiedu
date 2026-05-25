package com.ra.base_spring_boot.services.core.impl;

import com.ra.base_spring_boot.dto.resp.SongResp;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.Song;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.repository.ISongRepository;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.services.core.IWishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements IWishlistService {

    private final IUserRepository userRepository;
    private final ISongRepository songRepository;

    @Override
    public Set<SongResp> getWishlist(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getWishlistSongs()
                .stream()
                .map(this::mapToSongResp)
                 .collect(Collectors.toSet());
    }

    @Override
    public SongResp addToWishlist(Long userId, Long songId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new HttpNotFound("User not found"));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new HttpNotFound("Song not found"));

        if (user.getWishlistSongs().contains(song)) {
            throw new HttpNotFound("Song already in wishlist!");
        }

        user.getWishlistSongs().add(song);
        userRepository.save(user);
        return mapToSongResp(song);
    }

    @Override
    public void removeFromWishlist(Long userId, Long songId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        user.getWishlistSongs().remove(song);
        userRepository.save(user);
    }

    public SongResp mapToSongResp(Song song) {
        SongResp resp = new SongResp();

        resp.setId(song.getId());
        resp.setTitle(song.getTitle());
        resp.setDuration(song.getDuration());
        resp.setAlbum(song.getAlbum());
        resp.setArtist(song.getArtist());
        resp.setSongUrl(song.getSongUrl());
        resp.setViews(song.getViews());
        resp.setCreatedAt(song.getCreatedAt());
        resp.setUpdatedAt(song.getUpdatedAt());
        resp.setGenres(song.getGenres());
        return resp;
    }
}

