package com.example.pixel_perfection.service;

import com.example.pixel_perfection.entity.Role;
import com.example.pixel_perfection.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role findByName(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }
}
