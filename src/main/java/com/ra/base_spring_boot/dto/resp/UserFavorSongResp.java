package com.ra.base_spring_boot.dto.resp;

import com.ra.base_spring_boot.model.Song;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFavorSongResp {
    private Long id;
    private User userId;
    private Song songId;
}
