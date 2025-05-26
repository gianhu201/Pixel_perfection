package com.example.pixel_perfection.dto;

import com.example.pixel_perfection.entity.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse implements Serializable {
    @JsonProperty("access_token")
    private String accessToken;

    private UserLogin user;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserLogin implements Serializable {
        private long id;
        private String name;
        private String userName;
        private String password;
        private String email;

        private Role role;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInsideToken {
        private long id;
        private String email;
        private String name;
    }

}