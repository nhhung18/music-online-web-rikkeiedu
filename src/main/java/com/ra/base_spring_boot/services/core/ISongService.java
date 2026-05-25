
package com.ra.base_spring_boot.services.core;

import com.ra.base_spring_boot.dto.resp.SongResp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ISongService {
    List<SongResp> getSongsByAlbum(Long albumId);
    SongResp createSongToAlbum(Long artistId, Long albumId, String title, Integer duration, List<Long> genresId, MultipartFile songFile) throws Exception;
    void removeSongFromAlbum(Long albumId, Long songId);
    void deleteSongAndNotifyArtist(Long songId);
    List<SongResp> getSongsByArtist(Long artistId);

    void increaseView(Long songId);
    
    // Top songs methods
    List<SongResp> getTop15SongsByWeek();
    List<SongResp> getNewSong();
    List<SongResp> getTopSongsAllTime(int page, int size);
    List<SongResp> getTrendingTracks(int limit);
    List<SongResp> getRecentlyPlayedSongsByUser(int limit);
    void addToHistory(Long songId);
    void removeFromHistory(Long songId);
    void clearHistory();
    Object uploadSong(Long artistId, Long songId, MultipartFile file) throws Exception;
    Page<SongResp> getAllSong(Pageable pageable, String name);
    List<SongResp> getSongByGenre(Long id);
    Object uploadSongImage(Long artistId, Long songId, MultipartFile file) throws Exception;
}
