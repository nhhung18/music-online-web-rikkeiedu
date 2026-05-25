package com.ra.base_spring_boot.services.core;

import com.ra.base_spring_boot.model.Role;
import com.ra.base_spring_boot.model.constants.RoleName;

public interface IRoleService
{
    Role findByRoleName(RoleName roleName);
    void upgradeToArtist(Long userId);
    void downgradeToUser(Long userId);
}
