package com.ra.base_spring_boot.dto.resp;

import com.ra.base_spring_boot.model.Album;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SearchResp {
    private List<GenreResp> genres;
    private List<SongResp> songs;
    private List<AlbumResp> albums;
    private List<ArtistResp> artists;
}
