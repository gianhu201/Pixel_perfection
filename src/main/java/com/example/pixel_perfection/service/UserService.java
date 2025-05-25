package com.example.pixel_perfection.service;

import com.example.pixel_perfection.dto.UserDto;
import com.example.pixel_perfection.entity.Role;
import com.example.pixel_perfection.entity.User;
import com.example.pixel_perfection.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleService roleService;


    @Autowired
    private PasswordEncoder passwordEncoder;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public User save(User user) {
        // Encode password before saving
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(encodePassword(user.getPassword()));
        }
        return userRepository.save(user);
    }

    public User processOAuthPostLogin(String email, String name) {
        User existUser = findByEmail(email);

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setName(name);
        newUser.setUserName(email.split("@")[0]);
        newUser.setPassword(encodePassword(UUID.randomUUID().toString()));
        newUser.setStatus(true);
        Role userRole = roleService.findByName("USER");
        newUser.getRoles().add(userRole);
        return save(newUser);
    }

    public void updateUserToken(String token, String email) {
        User user = findByEmail(email);
        user.setRefreshToken(token);
        userRepository.save(user);
    }

}
