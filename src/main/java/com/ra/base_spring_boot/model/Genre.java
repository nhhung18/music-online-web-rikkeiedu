package com.ra.base_spring_boot.model;

import com.ra.base_spring_boot.model.base.BaseObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "genre")
@Getter
@Setter
@Builder
public class Genre extends BaseObject {
    @Column(name = "genre_name", unique = true, nullable = false)
    private String genreName;

    @Column(name = "genre_url")
    private String genreUrl;
}
