
package com.ra.base_spring_boot.services.core.impl;

import com.ra.base_spring_boot.dto.resp.CloudinaryResp;
import com.ra.base_spring_boot.dto.resp.SongResp;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.exception.HttpForbidden;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.*;
import com.ra.base_spring_boot.model.constants.RoleName;
import com.ra.base_spring_boot.model.constants.UserSongId;
import com.ra.base_spring_boot.repository.*;
import com.ra.base_spring_boot.services.core.IEmailService;
import com.ra.base_spring_boot.services.core.ISongService;
import com.ra.base_spring_boot.services.external.cloudinary.CloudinaryService;
import com.ra.base_spring_boot.util.FileUploadUtil;
import com.ra.base_spring_boot.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SongServiceImpl implements ISongService {
    private final ISongRepository songRepository;
    private final IAlbumRepository albumRepository;
    private final IUserRepository userRepository;
    private final IEmailService emailService;
    private final ISongHistoryRepository songHistoryRepository;
    private final CloudinaryService cloudinaryService;
    private final IGenreRepository genreRepository;
    private final ModelMapper modelMapper;

    public List<SongResp> getSongsByAlbum(Long albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("Album not found"));

        List<Song> songs = songRepository.findByAlbumId(albumId);

        return songs.stream()
                .map(song -> modelMapper.map(song, SongResp.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<SongResp> getSongsByArtist(Long artistId) {
        User artist = userRepository.findById(artistId)
                .orElseThrow(() -> new HttpNotFound("Artist not found"));

        boolean isArtist = artist.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equals(RoleName.ROLE_ARTIST));

        if (!isArtist) {
            throw new HttpBadRequest("User is not an artist");
        }

        List<Song> songs = songRepository.findByArtistId(artistId);

        return songs.stream()
                .map(song -> modelMapper.map(song, SongResp.class))
                .collect(Collectors.toList());
    }

    @Override
    public SongResp createSongToAlbum(Long artistId, Long albumId, String title, Integer duration, List<Long> genresId, MultipartFile songFile) throws Exception {
        User artist = userRepository.findArtistById(artistId);
        Album album = findAndValidateAlbum(artistId, albumId);
        Set<Genre> genres = genresId.stream()
                            .map(gid -> genreRepository.findById(gid)
                               .orElseThrow(() -> new HttpNotFound("Genre not found: " + gid)))
                            .collect(Collectors.toSet());

        Song song = Song.builder()
                        .title(title)
                        .duration(duration)
                        .artist(artist)
                        .album(album)
                        .genres(genres)
                        .createdAt(LocalDateTime.now())
                        .build();

        handleSongFileUpload(song, songFile);
        songRepository.save(song);
        return modelMapper.map(song, SongResp.class);
    }

    private Album findAndValidateAlbum(Long artistId, Long albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new HttpNotFound("Album not found"));

        if (!album.getArtist().getId().equals(artistId)) {
            throw new HttpForbidden("This is not your album");
        }

        return album;
    }

    private void handleSongFileUpload(Song song, MultipartFile songFile){
        if (songFile == null || songFile.isEmpty()) return;

        String fileName = FileUploadUtil.getFileName(songFile.getOriginalFilename());
        CloudinaryResp response = cloudinaryService.uploadSong(songFile, fileName);
        song.setSongUrl(response.getUrl());
    }

    public void removeSongFromAlbum(Long albumId, Long songId) {
        Long artistId = SecurityUtils.getCurrentUserId();
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        if (!songRepository.existsBySongIdAndArtistId(songId, artistId)) {
            throw new HttpNotFound("You don't have permission to delete this song");
        }

        songRepository.delete(song);
    }

    public void deleteSongAndNotifyArtist(Long songId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        songRepository.delete(song);

        String artistEmail = song.getArtist().getEmail();
        String subject = "Notification: Song Removed";
        String body = "The song '" + song.getTitle() + "' has been removed due to inappropriate content.";

        emailService.sendEmail(artistEmail, subject, body);
    }


    @Override
    public List<SongResp> getTopSongsAllTime(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Song> songs = songRepository.findTopSongsAllTime(pageable);
        return songs.stream()
                .map(song -> modelMapper.map(song, SongResp.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<SongResp> getTrendingTracks(int limit) {
        LocalDateTime trendingStart = LocalDateTime.now().minusDays(3);
        Pageable pageable = PageRequest.of(0, limit);
        List<Song> songs = songRepository.findTrendingTracks(trendingStart, pageable);
        return songs.stream()
                .map(song -> modelMapper.map(song, SongResp.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<SongResp> getTop15SongsByWeek() {
        Pageable pageable = PageRequest.of(0, 15);
        List<Song> songs = songRepository.findTop15ByOrderByViewsDesc();

        return songs.stream()
                .map(song -> modelMapper.map(song, SongResp.class))
                .collect(Collectors.toList());
    }


    @Override
    public List<SongResp> getRecentlyPlayedSongsByUser(int limit) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<Song> songs = songHistoryRepository.findRecentlyPlayedSongsByUser(userId, PageRequest.of(0, limit));
        return songs.stream()
                .map(song -> modelMapper.map(song, SongResp.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void increaseView(Long songId) {
        songRepository.incrementView(songId);
    }


    @Override
    @Transactional
    public void addToHistory(Long songId) {
        Long userId = SecurityUtils.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));
        
        UserSongId userSongId =UserSongId.builder()
                .userId(userId)
                .songId(songId)
                .build();

        SongHistory existingHistory = songHistoryRepository.findById(userSongId).orElse(null);

        if (existingHistory != null) {
            existingHistory.setPlayedAt(LocalDateTime.now());
            songHistoryRepository.save(existingHistory);
        } else {
            SongHistory songHistory = SongHistory.builder()
                    .id(userSongId)
                    .user(user)
                    .song(song)
                    .build();
            songHistoryRepository.save(songHistory);
        }
    }


    @Override
    public Object uploadSong(Long artistId, Long songId, MultipartFile file) throws Exception {
        User artist = userRepository.findArtistById(artistId);
        if (artist == null) {
            throw new RuntimeException("Artist not found");
        }
        Song song = songRepository.findById(songId).orElseThrow(() -> new RuntimeException());
        FileUploadUtil.assertAllowed(file, FileUploadUtil.IMAGE_PATTERN);
        final String fileName = FileUploadUtil.getFileName(file.getOriginalFilename());
        final CloudinaryResp response = cloudinaryService.uploadSong(file, fileName);
        song.setSongUrl(response.getUrl());
        songRepository.save(song);
        return null;
    }

    @Override
    public Page<SongResp> getAllSong(Pageable pageable, String title) {
        Page<Song> songs = null;
        if(title != null) {
            songs = songRepository.findByTitleContainingIgnoreCase(pageable, title);
        }else {
            songs = songRepository.findAll(pageable);
        }
        return songs.map(song -> modelMapper.map(song, SongResp.class));

    }

    @Override
    public List<SongResp> getSongByGenre(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new HttpNotFound("Genre not found"));
        List<Song> songs= songRepository.findByGenres(genre);

        return songs.stream()
                .map(song -> modelMapper.map(song, SongResp.class))
                .collect(Collectors.toList());
    }

    @Override
    public Object uploadSongImage(Long artistId, Long songId, MultipartFile file) throws Exception {
        User artist = userRepository.findArtistById(artistId);
        if (artist == null) {
            throw new RuntimeException("Artist not found");
        }
        Song song = songRepository.findById(songId).orElseThrow(() -> new RuntimeException());
        FileUploadUtil.assertAllowed(file, FileUploadUtil.IMAGE_PATTERN);
        final String fileName = FileUploadUtil.getFileName(file.getOriginalFilename());
        final CloudinaryResp response = cloudinaryService.uploadSongImage(file, fileName);
        song.setSongImgUrl(response.getUrl());
        songRepository.save(song);
        return null;
    }

    @Override
    public List<SongResp> getNewSong() {
        LocalDateTime dateThreshold = LocalDateTime.now().minusDays(7);
        Pageable pageable = PageRequest.of(0, 15);

        List<Song> songs = songRepository.findNewSongsWithHighViews(dateThreshold, pageable);

        return songs.stream()
                .map(song -> modelMapper.map(song, SongResp.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeFromHistory(Long songId) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        UserSongId userSongId = UserSongId.builder()
                .userId(userId)
                .songId(songId)
                .build();
        
        if (!songHistoryRepository.existsById(userSongId)) {
            throw new HttpNotFound("Song not found in your history");
        }

        songHistoryRepository.deleteByUserIdAndSongId(userId, songId);
    }

    @Override
    @Transactional
    public void clearHistory() {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        songHistoryRepository.deleteByUserId(userId);
    }

    private User getCurrentUser(){
        Long userId = SecurityUtils.getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

}
