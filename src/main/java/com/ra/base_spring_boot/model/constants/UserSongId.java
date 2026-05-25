
package com.ra.base_spring_boot.model.constants;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSongId implements Serializable {
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "song_id")
    private Long songId;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSongId that = (UserSongId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(songId, that.songId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId, songId);
    }
}
