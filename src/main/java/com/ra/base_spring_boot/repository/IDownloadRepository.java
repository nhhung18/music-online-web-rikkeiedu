package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Download;
import com.ra.base_spring_boot.model.Song;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.UserSongId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IDownloadRepository extends JpaRepository<Download, UserSongId> {
    List<Download> findAllByUser(User user);
    void deleteByUserIdAndSongId(User user, Song song);
}
