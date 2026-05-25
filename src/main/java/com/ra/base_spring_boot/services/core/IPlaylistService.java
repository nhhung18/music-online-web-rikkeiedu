package com.ra.base_spring_boot.services.core;

import com.ra.base_spring_boot.dto.req.PlaylistReq;
import com.ra.base_spring_boot.dto.resp.PlaylistResp;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IPlaylistService {
    List<PlaylistResp> searchPlaylists(String keyword);
    PlaylistResp createPlaylist(Long userId, PlaylistReq request);
    PlaylistResp addSongToPlaylist(Long userId, Long playlistId, Long songId);
    void removeSongFromPlaylist(Long userId, Long playlistId, Long songId);
    List<PlaylistResp> getPlaylists(Pageable pageable);
    PlaylistResp getPlaylistById(Long playlistId);
    PlaylistResp updatePlaylist(Long userId, Long playlistId, PlaylistReq request);
    void deletePlaylistById(Long userId, Long playlistId);
}
