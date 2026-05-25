
package com.ra.base_spring_boot.services.core.impl;

import com.ra.base_spring_boot.dto.resp.SongResp;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.Download;
import com.ra.base_spring_boot.model.Song;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.UserSongId;
import com.ra.base_spring_boot.repository.IDownloadRepository;
import com.ra.base_spring_boot.repository.ISongRepository;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.services.core.IDownloadService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DownloadServiceImpl implements IDownloadService {

    private final IDownloadRepository downloadRepository;
    private final IUserRepository userRepository;
    private final ISongRepository songRepository;
    private final ModelMapper modelMapper;

    // Thêm bài hát vào danh sách đã tải
    public void createDownload(Long userId, Long songId) {
        UserSongId downloadId = UserSongId.builder()
                .userId(userId)
                .songId(songId)
                .build();

        if (downloadRepository.existsById(downloadId)) {
            throw new HttpNotFound("Song already downloaded");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new HttpNotFound("User not found"));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new HttpNotFound("Song not found"));

        Download download = Download.builder()
                .id(downloadId)
                .user(user)
                .song(song)
                .build();

        downloadRepository.save(download);
    }


    public List<SongResp> getDownloadsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new HttpNotFound("User not found"));

        List<Download> downloads = downloadRepository.findAllByUser(user);

        return downloads.stream()
                .map(this::mapToSongResp)
                .collect(Collectors.toList());
    }


    public void deleteDownload(Long userId, Long songId) {
        UserSongId downloadId = UserSongId.builder()
                .userId(userId)
                .songId(songId)
                .build();

        if (!downloadRepository.existsById(downloadId)) {
            throw new HttpNotFound("Download not found");
        }

        downloadRepository.deleteById(downloadId);
    }

    private SongResp mapToSongResp(Download download){
        Song s = download.getSong();
        SongResp sResp = new SongResp();
        sResp.setId(s.getId());
        sResp.setTitle(s.getTitle());
        sResp.setDuration(s.getDuration());
        sResp.setAlbum(s.getAlbum());
        sResp.setArtist(s.getArtist());
        sResp.setSongUrl(s.getSongUrl());
        sResp.setSongImgUrl(s.getSongImgUrl());
        sResp.setViews(s.getViews());
        sResp.setCreatedAt(s.getCreatedAt());
        sResp.setUpdatedAt(s.getUpdatedAt());
        sResp.setGenres(s.getGenres());
        return sResp;
    }

}
