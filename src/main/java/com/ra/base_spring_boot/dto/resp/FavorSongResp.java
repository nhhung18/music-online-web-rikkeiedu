package com.ra.base_spring_boot.dto.resp;

import com.ra.base_spring_boot.model.Song;
import com.ra.base_spring_boot.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavorSongResp {
    private User userId;
    private Song songId;
}
