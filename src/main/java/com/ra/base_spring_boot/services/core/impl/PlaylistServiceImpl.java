package com.ra.base_spring_boot.services.core.impl;

import com.ra.base_spring_boot.dto.req.PlaylistReq;
import com.ra.base_spring_boot.dto.resp.PlaylistResp;
import com.ra.base_spring_boot.dto.resp.SongResp;
import com.ra.base_spring_boot.exception.HttpForbidden;
import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.Playlist;
import com.ra.base_spring_boot.model.PlaylistSong;
import com.ra.base_spring_boot.model.Song;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.PlaylistSongId;
import com.ra.base_spring_boot.repository.IPlaylistRepository;
import com.ra.base_spring_boot.repository.ISongRepository;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.services.core.IPlaylistService;
import com.ra.base_spring_boot.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaylistServiceImpl implements IPlaylistService {

    private final IPlaylistRepository playlistRepository;
    private  final ISongRepository songRepository;
    private final IUserRepository userRepository;

    public List<PlaylistResp> searchPlaylists(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return new ArrayList<>();
        }
        Long userId= SecurityUtils.getCurrentUserIdOrNull();
        List<Playlist> playlists = playlistRepository.searchPlaylists(keyword, userId);

        return playlists.stream()
                .map(this::mapToPlaylistResp)
                .collect(Collectors.toList());
    }

    public PlaylistResp createPlaylist(Long userId, PlaylistReq request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new HttpNotFound("User not found"));
        Playlist playlist = new Playlist();
        playlist.setName(request.getName());
        playlist.setIsPublic(request.getIsPublic());
        playlist.setUser(user);

        playlistRepository.save(playlist);

        return mapToPlaylistResp(playlistRepository.save(playlist));
    }


    public PlaylistResp addSongToPlaylist(Long userId, Long playlistId, Long songId) {
        Playlist playlist = findAndValidatePlaylist(userId,playlistId);

        Song song = findAndEnsureSongNotExistInPlaylist(playlist, songId);

        PlaylistSong playlistSong = new PlaylistSong();
        PlaylistSongId id = new PlaylistSongId(playlist.getId(), song.getId());
        playlistSong.setId(id);
        playlistSong.setPlaylist(playlist);
        playlistSong.setSong(song);
        playlist.getPlaylistSongs().add(playlistSong);

        return mapToPlaylistResp(playlistRepository.save(playlist));
    }

    private Song findAndEnsureSongNotExistInPlaylist(Playlist playlist, Long songId){
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new HttpNotFound("Song not found"));

        boolean exists = playlist.getPlaylistSongs().stream()
                .anyMatch(ps -> ps.getSong().getId().equals(songId));
        if (exists) {
            throw new HttpForbidden("Song already in playlist");
        }

        return song;
    }


    public void removeSongFromPlaylist(Long userId, Long playlistId, Long songId) {
        Playlist playlist = findAndValidatePlaylist(userId,playlistId);

        playlist.getPlaylistSongs().removeIf(ps -> ps.getSong().getId().equals(songId));

        playlistRepository.save(playlist);
    }

    private Playlist findAndValidatePlaylist(Long userId, Long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new HttpNotFound("Playlist not found"));

        if (!playlist.getUser().getId().equals(userId)) {
            throw new HttpForbidden("You don't have permission");
        }

        return playlist;
    }

    @Override
    public List<PlaylistResp> getPlaylists(Pageable pageable) {
        Long userId= SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(()->new HttpNotFound("User not found"));
        List<Playlist> playlists = playlistRepository.findByUser(user, pageable);

        return playlists.stream()
                .map(this::mapToPlaylistResp)
                .collect(Collectors.toList());

    }

    @Override
    public PlaylistResp getPlaylistById(Long playlistId) {
        Playlist playlist=playlistRepository.findById(playlistId)
                .orElseThrow(()-> new HttpNotFound("Playlist not found"));
        if(!playlist.getIsPublic()){
            Long userId = SecurityUtils.getCurrentUserId();
            if(!playlist.getUser().getId().equals(userId)){
                throw new RuntimeException("You don't have permission");
            }
        };

        return mapToPlaylistResp(playlist);
    }

    @Override
    public PlaylistResp updatePlaylist(Long userId, Long playlistId, PlaylistReq request) {
        Playlist playlist = findAndValidatePlaylist(userId, playlistId);

        if (request.getName() != null) {
            playlist.setName(request.getName());
        }

        if (request.getIsPublic() != null) {
            playlist.setIsPublic(request.getIsPublic());
        }

        Playlist updatedPlaylist = playlistRepository.save(playlist);
        return mapToPlaylistResp(updatedPlaylist);
    }

    @Override
    public void deletePlaylistById(Long userId, Long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new HttpNotFound("Playlist not found"));

        if (!playlist.getUser().getId().equals(userId)) {
            throw new HttpForbidden("You don't have permission to delete this playlist");
        }

        playlistRepository.deleteById(playlistId);
    }

    public PlaylistResp mapToPlaylistResp(Playlist playlist) {
        PlaylistResp resp = new PlaylistResp();

        resp.setId(playlist.getId());
        resp.setName(playlist.getName());
        resp.setIsPublic(playlist.getIsPublic());
        resp.setUserId(playlist.getUser().getId());
        resp.setUsername(playlist.getUser().getFirstName() + " " + playlist.getUser().getLastName());

        List<SongResp> songs = new ArrayList<>();
        if (playlist.getPlaylistSongs() != null) {
            for (PlaylistSong playlistSong : playlist.getPlaylistSongs()) {
                Song song = playlistSong.getSong();
                SongResp songResp = new SongResp();
                songResp.setId(song.getId());
                songResp.setTitle(song.getTitle());
                songResp.setAlbum(song.getAlbum());
                songResp.setDuration(song.getDuration());
                songResp.setArtist(song.getArtist());
                songResp.setSongUrl(song.getSongUrl());
                songResp.setSongImgUrl(song.getSongImgUrl());
                songResp.setViews(song.getViews());
                songResp.setCreatedAt(song.getCreatedAt());
                songResp.setUpdatedAt(song.getUpdatedAt());
                songResp.setGenres(song.getGenres());
                songs.add(songResp);
            }
        }
        resp.setSongs(songs);

        return resp;
    }



}
