package com.example.pixel_perfection.controller;

import com.example.pixel_perfection.dto.GoogleLoginRequest;
import com.example.pixel_perfection.dto.LoginResponse;
import com.example.pixel_perfection.dto.UserDto;
import com.example.pixel_perfection.entity.Role;
import com.example.pixel_perfection.entity.User;
import com.example.pixel_perfection.service.RoleService;
import com.example.pixel_perfection.service.UserService;
import com.example.pixel_perfection.util.SecurityUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SecurityUtil securityUtil;
    @Autowired
    private RoleService roleService;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    @GetMapping("/login")
    public void loginRedirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }


    @GetMapping("/login/success")
    public ResponseEntity<?> loginSuccess(OAuth2AuthenticationToken authentication) {
        Map<String, Object> attributes = authentication.getPrincipal().getAttributes();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String userName = email.split("@")[0];

        User user = userService.findByEmail(email);

        if (!user.isStatus()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Tài khoản đã bị khóa");
        }

        // Tạo JWT (access token + refresh token)
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setUser(LoginResponse.UserLogin.builder()
                .id(user.getId())
                .email(user.getEmail())
                .userName(user.getUserName())
                .name(user.getName())
                .role(user.getRoles().stream().findFirst().orElse(null))
                .build());

        String accessToken = securityUtil.createAccessToken(user.getUserName(), loginResponse);
        String refreshToken = securityUtil.createRefreshToken(user.getUserName(), loginResponse);

        userService.updateUserToken(refreshToken, user.getEmail());

        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        loginResponse.setAccessToken(accessToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(loginResponse);
    }




    @GetMapping("/login/failure")
    public Map<String, String> loginFailure() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Đăng nhập thất bại!");
        return response;
    }

    //    @GetMapping("/login/success")
//    public Map<String, Object> getLoginInfo(OAuth2AuthenticationToken authentication) {
//        Map<String, Object> response = new HashMap<>();
//        Map<String, Object> attributes = authentication.getPrincipal().getAttributes();
//
//        String email = (String) attributes.get("email");
//        String name = (String) attributes.get("name");
//
//        User user = userService.processOAuthPostLogin(email, name);
//
//        UserDto userDto = new UserDto();
//        userDto.setId(user.getId());
//        userDto.setEmail(user.getEmail());
//        userDto.setName(user.getName());
//        userDto.setUsername(user.getUserName());
//        userDto.setStatus(user.isStatus());
//
//        response.put("user", userDto);
//        response.put("message", "Đăng nhập thành công với Google!");
//
//        return response;
//    }
//    @PostMapping("/google")
//    public ResponseEntity<?> googleAuth(@RequestBody GoogleLoginRequest googleLoginRequest) {
//        String googleAccessToken = googleLoginRequest.getAccessToken();
//        String googleUrl = "https://www.googleapis.com/oauth2/v3/userinfo";
//        RestTemplate restTemplate = new RestTemplate();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(googleAccessToken);
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//
//        ResponseEntity<Map> googleResponse = restTemplate.exchange(googleUrl, HttpMethod.GET, entity, Map.class);
//
//        if (googleResponse.getStatusCode() == HttpStatus.OK) {
//            Map<String, Object> userInfo = googleResponse.getBody();
//
//            String email = (String) userInfo.get("email");
//            String firstName = (String) userInfo.get("given_name");
//            String lastName = (String) userInfo.get("family_name");
//
//            // Tạo userName theo email (hoặc có thể theo firstName + lastName)
//            String userName = email.split("@")[0]; // ví dụ lấy phần trước @ làm userName
//
//            User existingUser = userService.findByEmail(email);
//
//            if (existingUser == null) {
//                // Tạo user mới
//                String randomPassword = UUID.randomUUID().toString();
//                User newUser = new User();
//                newUser.setEmail(email);
//                newUser.setUserName(userName);
//                newUser.setName(firstName + " " + lastName);
//                newUser.setPassword(passwordEncoder.encode(randomPassword));
//                newUser.setStatus(true);  // active luôn
//                // set mac dinh user role
//                Role userRole = roleService.findByName("USER");
//                newUser.getRoles().add(userRole);
//
//                existingUser = userService.save(newUser);  // Lưu user mới
//            }
//
//            if (!existingUser.isStatus()) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body("User account is disabled.");
//            }
//
//            // Set authentication thủ công
//            UsernamePasswordAuthenticationToken authenticationToken =
//                    new UsernamePasswordAuthenticationToken(existingUser.getUserName(), null);
//            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//
//            // Tạo response user info (LoginResponse có thể sửa lại theo User entity này)
//            LoginResponse loginResponse = new LoginResponse();
//            loginResponse.setUser(LoginResponse.UserLogin.builder()
//                    .id(existingUser.getId())
//                    .email(existingUser.getEmail())
//                    .userName(existingUser.getUserName())
//                    .name(existingUser.getName())
//                    .build());
//
//            // Tạo access token
//            String accessTokenInternal = securityUtil.createAccessToken(existingUser.getUserName(), loginResponse);
//            loginResponse.setAccessToken(accessTokenInternal);
//
//            // Tạo refresh token
//            String refreshToken = securityUtil.createRefreshToken(existingUser.getUserName(), loginResponse);
//
//            // Cập nhật refresh token trong DB
//            userService.updateUserToken(refreshToken, existingUser.getEmail());
//
//            // Tạo cookie refresh token
//            ResponseCookie responseCookie = ResponseCookie.from("refresh_token", refreshToken)
//                    .httpOnly(true)
//                    .secure(true)
//                    .path("/")
//                    .maxAge(refreshTokenExpiration)
//                    .build();
//
//            return ResponseEntity.status(HttpStatus.OK)
//                    .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
//                    .body(loginResponse);
//
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Google access token");
//        }
//    }
//

}
