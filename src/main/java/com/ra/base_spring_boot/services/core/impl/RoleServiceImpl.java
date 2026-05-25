package com.ra.base_spring_boot.services.core.impl;

import com.ra.base_spring_boot.exception.HttpNotFound;
import com.ra.base_spring_boot.model.Role;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.RoleName;
import com.ra.base_spring_boot.repository.IRoleRepository;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.services.core.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements IRoleService
{
    private final IRoleRepository roleRepository;
    private final IUserRepository userRepository;

    @Override
    public Role findByRoleName(RoleName roleName)
    {
        return roleRepository.findByRoleName(roleName).orElseThrow(() -> new HttpNotFound("role not found"));
    }

    @Override
    public void upgradeToArtist(Long userId) {
        User user= userRepository.findById(userId)
                .orElseThrow(()-> new HttpNotFound("User not found"));
        Set<Role> roles= new HashSet<>();
        Role role = findByRoleName(RoleName.ROLE_ARTIST);
        roles.add(role);
        user.setRoles(roles);
        userRepository.save(user);
    }

    @Override
    public void downgradeToUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new HttpNotFound("User not found"));
        Set<Role> roles = new HashSet<>();
        Role role = findByRoleName(RoleName.ROLE_USER);
        roles.add(role);
        user.setRoles(roles);
        userRepository.save(user);
    }
}
