package com.ra.base_spring_boot.dto.resp;

import com.ra.base_spring_boot.model.Song;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.UserSongId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DownloadResp {
    private UserSongId id;
    private User user;
    private Song song;
    private LocalDateTime addedAt;

    public Long getUser() {
        return user.getId();
    }
}
