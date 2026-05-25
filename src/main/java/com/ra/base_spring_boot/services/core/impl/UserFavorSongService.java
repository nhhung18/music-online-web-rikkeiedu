package com.ra.base_spring_boot.services.core.impl;

import com.ra.base_spring_boot.model.Song;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.UserFavorSong;
import com.ra.base_spring_boot.repository.ISongRepository;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.repository.IuserFavorSongRepository;
import com.ra.base_spring_boot.services.core.IUserFavorSongService;
import com.ra.base_spring_boot.util.SecurityUtils;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserFavorSongService implements IUserFavorSongService {

    @Autowired
    private IuserFavorSongRepository iuserFavorSongRepository;

    @Autowired
    private IUserRepository iUserRepository;

    @Autowired
    private ISongRepository iSongRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserFavorSong addFavorSong(Long userId, Long songId) {
        iuserFavorSongRepository.findByUserIdAndSongId(userId, songId).ifPresent(song -> {
            throw new RuntimeException("Song already in favorites!");
        });
        User user = iUserRepository.findById(userId).orElseThrow(() -> new RuntimeException("User id does not exist!"));
        Song song = iSongRepository.findById(songId).orElseThrow(() -> new RuntimeException("Song id does not exist!"));
        UserFavorSong favorSong = new UserFavorSong();
        favorSong.setUser(user);
        favorSong.setSong(song);
        return iuserFavorSongRepository.save(favorSong);
    }

    @Override
    public List<UserFavorSong> getFavorSong() {
        Long userId = SecurityUtils.getCurrentUserId();
        return iuserFavorSongRepository.findAllByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteFavorById(Long userId, Long songId) throws Exception {
        User user = iUserRepository.findById(userId).orElseThrow(() -> new RuntimeException("User id does not exist!"));
        iuserFavorSongRepository.deleteAllByUserIdAndSongId(userId, songId);
    }
}
