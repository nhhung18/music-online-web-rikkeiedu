
package com.ra.base_spring_boot.services.core.impl;

import com.ra.base_spring_boot.dto.req.AlbumReq;
import com.ra.base_spring_boot.dto.resp.AlbumResp;
import com.ra.base_spring_boot.dto.resp.CloudinaryResp;
import com.ra.base_spring_boot.exception.FuncErrorException;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.exception.HttpForbidden;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.*;
import com.ra.base_spring_boot.model.constants.RoleName;
import com.ra.base_spring_boot.repository.IAlbumRepository;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.services.core.IAlbumService;
import com.ra.base_spring_boot.services.core.IEmailService;
import com.ra.base_spring_boot.services.external.cloudinary.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements IAlbumService {

    private final IAlbumRepository albumRepository;
    private final IUserRepository userRepository;
    private final IEmailService emailService;
    private final ModelMapper modelMapper;
    private final CloudinaryService cloudinaryService;

    @Override
    public List<Album> getAllByArtist(Long artistId) {
        if (!userRepository.existsById(artistId)) {
            throw new RuntimeException("Artist not found");
        }
        
        if (!userRepository.existsByUserIdAndRoleArtist(artistId)) {
            throw new RuntimeException("User is not an artist");
        }
        
        return albumRepository.findAllByArtist_Id(artistId);
    }

    public List<Album> getSongByAlbum(Long artistId) {
        User artist = userRepository.findById(artistId)
                .orElseThrow(() -> new HttpNotFound("Artist not found"));
        // Check if user has ARTIST role
        boolean isArtist = artist.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equals(RoleName.ROLE_ARTIST));

        if (!isArtist) {
            throw new HttpBadRequest("User is not an artist");
        }

        return albumRepository.findAllByArtist_Id(artistId);
    }


    @Override
    public Album createAlbum(Long artistId, AlbumReq album) {
        User artist = userRepository.findById(artistId)
                .orElseThrow(() -> new RuntimeException("Artist not found"));

        Album album1 = Album.builder()
                .title(album.getTitle())
                .releaseDate(album.getReleaseDate())
                .coverImage(album.getCoverImage())
                .artist(artist)
                .build();

        return albumRepository.save(album1);
    }

    @Override
    public Album updateAlbum(Long artistId, Long albumId, AlbumReq albumReq, MultipartFile coverImage) {
        Album album = findAndValidateAlbum(artistId,albumId);

        if (albumReq.getTitle() != null) {
            album.setTitle(albumReq.getTitle());
        }

        if (albumReq.getReleaseDate() != null) {
            album.setReleaseDate(albumReq.getReleaseDate());
        }

        if (coverImage != null && !coverImage.isEmpty()) {
            try {
                CloudinaryResp cloudinaryResp = cloudinaryService.uploadAlbumCover(
                        coverImage,
                        "album_" + albumId + "_" + System.currentTimeMillis()
                );
                album.setCoverImage(cloudinaryResp.getUrl());
            } catch (Exception e) {
                throw new FuncErrorException("Failed to upload album cover image");
            }
        }
        album.setUpdatedAt(LocalDateTime.now());

        return albumRepository.save(album);
    }

    @Override
    public Page<AlbumResp> getAllAlbums(Pageable pageable, String title) {
        Page<Album> albumPage = null;
        if(title != null) {
            albumPage = albumRepository.findByTitleContainingIgnoreCase(pageable, title);
        }else {
            albumPage = albumRepository.findAll(pageable);
        }
        return albumPage.map(this::mapToAlbumResp);
    }


    @Override
    public void deleteAlbum(Long artistId, Long albumId) {

        if (!albumRepository.existsByAlbumIdAndArtistId(albumId, artistId)) {
            throw new HttpNotFound("Album not found or you don't have permission to delete this album");
        }

        albumRepository.deleteById(albumId);
    }

    public void deleteAlbumAndNotifyArtist(Long albumId) {

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("Album not found"));

        User artist = album.getArtist();
        String artistEmail = artist.getEmail();

        albumRepository.delete(album);

        String subject = "Notification: Album Removed";
        String body = "Your album '" + album.getTitle() + "' has been removed due to inappropriate content.";

        emailService.sendEmail(artistEmail, subject, body);
    }


    @Override
    public List<AlbumResp> getFeaturedAlbums() {
        Pageable pageable = PageRequest.of(0, 10); // Lấy 10 album nổi bật
        List<Album> albums = albumRepository.findFeaturedAlbums(pageable);
        return albums.stream()
                .map(this::mapToAlbumResp)
                .collect(Collectors.toList());
    }

    @Override
    public List<AlbumResp> getTop15Albums() {
        Pageable pageable = PageRequest.of(0, 15);
        List<Album> albums = albumRepository.findTop15Albums(pageable);
        return albums.stream()
                .map(this::mapToAlbumResp)
                .collect(Collectors.toList());
    }

    @Override
    public List<AlbumResp> getNewAlbumsThisWeek() {
        Pageable pageable = PageRequest.of(0, 15);
        
        List<Album> albums = albumRepository.findNewAlbumsThisWeek(pageable);
        return albums.stream()
                .map(this::mapToAlbumResp)
                .collect(Collectors.toList());
    }

    @Override
    public AlbumResp getAlbumById(Long albumId) {
        Album album = albumRepository.findById(albumId)
               .orElseThrow(() -> new HttpNotFound("Album not found"));
        return mapToAlbumResp(album);
    }

    private Album findAndValidateAlbum(Long artistId, Long albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new HttpNotFound("Album not found"));

        if (!album.getArtist().getId().equals(artistId)) {
            throw new HttpForbidden("You don't have permission to update this album");
        }

        return album;
    }


    public AlbumResp mapToAlbumResp(Album album) {
        AlbumResp resp = new AlbumResp();

        resp.setId(album.getId());
        resp.setCoverImage(album.getCoverImage());
        resp.setCreatedAt(album.getCreatedAt());
        resp.setUpdatedAt(album.getUpdatedAt());
        resp.setReleaseDate(album.getReleaseDate());
        resp.setTitle(album.getTitle());
        resp.setArtistId(album.getArtist().getId());
        resp.setArtistName(album.getArtist().getFirstName() + " " + album.getArtist().getLastName());

        // Tính tổng views từ tất cả bài hát trong album
        long totalViews = 0L;
        if (album.getSongs() != null && !album.getSongs().isEmpty()) {
            totalViews = album.getSongs().stream()
                    .mapToLong(song -> song.getViews() != null ? song.getViews() : 0L)
                    .sum();
        }
        resp.setViews(totalViews);

        return resp;
    }

}
