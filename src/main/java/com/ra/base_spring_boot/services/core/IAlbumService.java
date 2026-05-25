
package com.ra.base_spring_boot.services.core;

import com.ra.base_spring_boot.dto.req.AlbumReq;
import com.ra.base_spring_boot.dto.resp.AlbumResp;
import com.ra.base_spring_boot.model.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IAlbumService {
    List<Album> getAllByArtist(Long artistId);
    Album createAlbum(Long artistId, AlbumReq album);
    Album updateAlbum(Long artistId, Long albumId, AlbumReq albumReq, MultipartFile coverImage
    );
    void deleteAlbum(Long artistId, Long albumId);
    void deleteAlbumAndNotifyArtist(Long albumId);
    List<AlbumResp> getFeaturedAlbums();
    List<AlbumResp> getTop15Albums();
    Page<AlbumResp> getAllAlbums(Pageable pageable, String title);
    List<Album> getSongByAlbum(Long artistId);
    List<AlbumResp> getNewAlbumsThisWeek();
    AlbumResp getAlbumById(Long albumId);
}
