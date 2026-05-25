package com.ra.base_spring_boot.services.external.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ra.base_spring_boot.dto.resp.CloudinaryResp;
import com.ra.base_spring_boot.exception.FuncErrorException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class CloudinaryService {
    @Autowired
    private Cloudinary cloudinary;

    @Transactional
    public CloudinaryResp uploadBanner(final MultipartFile file, final String fileName) {
        try {
            final Map result = this.cloudinary.uploader()
                    .upload(file.getBytes(),
                            ObjectUtils.asMap(
                                    "resource_type", "auto",   // auto: hỗ trợ image, video, audio, pdf...
                                    "folder", "Banner"           // Tên folder trên Cloudinary
                            ));
            final String imageUrl = (String) result.get("secure_url");
//            final String publicId = (String) result.get("public_id");
            return CloudinaryResp.builder().url(imageUrl)
                    .build();
        } catch (final Exception e) {
            throw new FuncErrorException("Failed to upload file!");
        }
    }

    @Transactional
    public CloudinaryResp uploadProfile(final MultipartFile file, final String fileName) {
        try {
            final Map result = this.cloudinary.uploader()
                    .upload(file.getBytes(),
                            ObjectUtils.asMap(
                                    "resource_type", "auto",   // auto: hỗ trợ image, video, audio, pdf...
                                    "folder", "Profile Avatar"           // Tên folder trên Cloudinary
                            ));
            final String imageUrl = (String) result.get("secure_url");
//            final String publicId = (String) result.get("public_id");
            return CloudinaryResp.builder().url(imageUrl)
                    .build();
        } catch (final Exception e) {
            throw new FuncErrorException("Failed to upload file!");
        }
    }

    @Transactional
    public CloudinaryResp uploadSong(final MultipartFile file, final String fileName) {
        try {
            final Map result = this.cloudinary.uploader()
                    .upload(file.getBytes(),
                            ObjectUtils.asMap(
                                    "resource_type", "auto",   // auto: hỗ trợ image, video, audio, pdf...
                                    "folder", "Song"           // Tên folder trên Cloudinary
                            ));
            final String songUrl = (String) result.get("secure_url");
//            final String publicId = (String) result.get("public_id");
            return CloudinaryResp.builder().url(songUrl)
                    .build();
        } catch (final Exception e) {
            throw new FuncErrorException("Failed to upload file!");
        }
    }

    @Transactional
    public CloudinaryResp uploadGenre(final MultipartFile file, final String fileName) {
        try {
            final Map result = this.cloudinary.uploader()
                    .upload(file.getBytes(),
                            ObjectUtils.asMap(
                                    "resource_type", "auto",   // auto: hỗ trợ image, video, audio, pdf...
                                    "folder", "Genre"           // Tên folder trên Cloudinary
                            ));
            final String imageUrl = (String) result.get("secure_url");
//            final String publicId = (String) result.get("public_id");
            return CloudinaryResp.builder().url(imageUrl)
                    .build();
        } catch (final Exception e) {
            throw new FuncErrorException("Failed to upload file!");
        }
    }

    @Transactional
    public CloudinaryResp uploadSongImage(final MultipartFile file, final String fileName) {
        try {
            final Map result = this.cloudinary.uploader()
                    .upload(file.getBytes(),
                            ObjectUtils.asMap(
                                    "resource_type", "auto",   // auto: hỗ trợ image, video, audio, pdf...
                                    "folder", "Song Image"           // Tên folder trên Cloudinary
                            ));
            final String imageUrl = (String) result.get("secure_url");
//            final String publicId = (String) result.get("public_id");
            return CloudinaryResp.builder().url(imageUrl)
                    .build();
        } catch (final Exception e) {
            throw new FuncErrorException("Failed to upload file!");
        }
    }

    @Transactional
    public CloudinaryResp uploadAlbumCover(final MultipartFile file, final String fileName) {
        try {
            final Map result = this.cloudinary.uploader()
                    .upload(file.getBytes(),
                            ObjectUtils.asMap(
                                    "resource_type", "auto",
                                    "folder", "Album Cover"     // Tên folder cho ảnh bìa album
                            ));
            final String imageUrl = (String) result.get("secure_url");
            return CloudinaryResp.builder().url(imageUrl)
                    .build();
        } catch (final Exception e) {
            throw new FuncErrorException("Failed to upload album cover!");
        }
    }
}
