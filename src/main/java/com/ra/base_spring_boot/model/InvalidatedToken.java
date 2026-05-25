package com.ra.base_spring_boot.model;

import com.ra.base_spring_boot.model.base.BaseObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "invalidatedtoken")
@Getter
@Setter
@Builder
public class InvalidatedToken {
    @Id
    private String id;
    @Column(name = "expiry_time")
    private Date expiryTime;
}
