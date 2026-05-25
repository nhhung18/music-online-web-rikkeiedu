package com.ra.base_spring_boot.model;

import com.ra.base_spring_boot.model.base.BaseObject;
import com.ra.base_spring_boot.model.constants.RoleName;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "role")
@Getter
@Setter
@Builder
public class Role extends BaseObject
{
    @Enumerated(EnumType.STRING)
    @Column(name = "role_name", columnDefinition = "ENUM('ROLE_ADMIN', 'ROLE_ARTIST', 'ROLE_USER')")
    private RoleName roleName;
}