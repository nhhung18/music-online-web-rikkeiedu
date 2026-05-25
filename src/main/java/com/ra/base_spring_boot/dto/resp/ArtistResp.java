
package com.ra.base_spring_boot.dto.resp;

import com.ra.base_spring_boot.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistResp {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String profileImage;
    private String bio;
    
    // Statistics
    private Long totalAlbums;
    private Long totalSongs;
    private Long totalViews;
    private Long totalPlays;
    
    // Static mapper method
    public static ArtistResp fromUser(User user) {
        if (user == null) {
            return null;
        }
        
        // Calculate statistics
        Long totalAlbums = (long) (user.getAlbums() != null ? user.getAlbums().size() : 0);
        Long totalSongs = (long) (user.getSongs() != null ? user.getSongs().size() : 0);
        Long totalViews = user.getSongs() != null ? 
            user.getSongs().stream().mapToLong(song -> song.getViews() != null ? song.getViews() : 0).sum() : 0;
        Long totalPlays = (long) (user.getSongHistories() != null ? user.getSongHistories().size() : 0);
        
        return ArtistResp.builder()
                .id(user.getId())
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .email(user.getEmail())
                .profileImage(user.getProfileImage())
                .bio(user.getBio())
                .totalAlbums(totalAlbums)
                .totalSongs(totalSongs)
                .totalViews(totalViews)
                .totalPlays(totalPlays)
                .build();
    }
}
