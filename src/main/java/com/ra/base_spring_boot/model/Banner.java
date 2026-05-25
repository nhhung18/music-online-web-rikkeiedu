package com.ra.base_spring_boot.model;

import com.ra.base_spring_boot.model.base.BaseObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Data
@Table(name = "banner")
@AllArgsConstructor
@NoArgsConstructor
public class Banner extends BaseObject implements Serializable {
    @Column(name = "title", nullable = false, unique = true)
    private String title;
//    private String publicId;
    private String imageUrl;
}
