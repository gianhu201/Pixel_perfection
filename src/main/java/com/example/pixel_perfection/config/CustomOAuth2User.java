//package com.example.pixel_perfection.security;
//
//import com.example.pixel_perfection.entity.User;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
//
//import java.util.Collection;
//import java.util.Map;
//
//public class CustomOAuth2User extends DefaultOAuth2User {
//
//    private final User user;
//
//    public CustomOAuth2User(User user, Map<String, Object> attributes, Collection<? extends GrantedAuthority> authorities) {
//        super(authorities, attributes, "email");  // "email" là key định danh trong attributes
//        this.user = user;
//    }
//
//    public User getUser() {
//        return user;
//    }
//}
