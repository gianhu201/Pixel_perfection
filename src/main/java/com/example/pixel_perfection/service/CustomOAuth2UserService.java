package com.example.pixel_perfection.service;

import com.example.pixel_perfection.entity.Role;
import com.example.pixel_perfection.entity.User;
import com.example.pixel_perfection.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(oauth2User);
        } catch (Exception ex) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("invalid_token"),
                    ex.getMessage(),
                    ex
            );
        }
    }

    private OAuth2User processOAuth2User(OAuth2User oauth2User) {
        Map<String, Object> attributes = oauth2User.getAttributes();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        if (email == null || email.isEmpty()) {
            throw new RuntimeException("Email not found from OAuth2 provider");
        }

        User user = userService.findByEmail(email);

        if (user == null) {
            // Tạo user mới nếu chưa có trong database
            user = new User();
            user.setEmail(email);
            user.setName(name != null ? name : email.split("@")[0]);
            user.setUserName(email.split("@")[0]);
            user.setPassword(UUID.randomUUID().toString()); // password random
            user.setStatus(true);

            Role userRole = roleRepository.findByRoleName("USER");
            if (userRole == null) {
                throw new RuntimeException("Default role USER not found");
            }
            user.getRoles().add(userRole);

            userService.save(user);
        }

        // Trả về đối tượng OAuth2User ban đầu (hoặc có thể bọc lại tùy nhu cầu)
        return oauth2User;
    }
}
