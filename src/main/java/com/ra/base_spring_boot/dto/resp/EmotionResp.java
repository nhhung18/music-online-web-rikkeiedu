package com.ra.base_spring_boot.dto.resp;

import com.ra.base_spring_boot.model.Genre;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class EmotionResp {
    String emotion;
    Genre genre;
}
