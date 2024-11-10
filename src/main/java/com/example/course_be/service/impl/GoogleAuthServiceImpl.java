package com.example.course_be.service.impl;

import com.example.course_be.entity.Role;
import com.example.course_be.entity.User;
import com.example.course_be.repository.RoleRepository;
import com.example.course_be.repository.UserRepository;
import com.example.course_be.service.GoogleAuthService;
import com.example.course_be.service.JwtService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class GoogleAuthServiceImpl implements GoogleAuthService {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtService jwtService;

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    @Override
    public Map<String, Object> googleLogin(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY)
                    .setAudience(Collections.singletonList(clientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String userId = payload.getSubject();
                String email = payload.getEmail();
                String name = (String) payload.get("name");
                String pictureUrl = (String) payload.get("picture");

                User user = userRepository.findByEmail(email);

                if (user == null) {
                    user = new User();
                    user.setUserName(email);
                    user.setEmail(email);
                    user.setFullName(name);
                    user.setAvatar(pictureUrl);
                    user.setGoogleId(userId);

                    // Assign the CUSTOMER role to the new user
                    Role customerRole = roleRepository.findByRoleName("CUSTOMER");
                    if (customerRole == null) {
                        customerRole = new Role();
                        customerRole.setRoleName("CUSTOMER");
                        customerRole = roleRepository.save(customerRole);
                    }
                    user.setListRoles(Collections.singletonList(customerRole));
                    userRepository.save(user);
                }

                String token = jwtService.generateToken(user.getUserName());

                // Return a response with token, avatar, and roles
                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("avatar", user.getAvatar());
                response.put("role", user.getListRoles().stream().findFirst().get().getRoleName());

                return response;
            } else {
                throw new RuntimeException("Invalid ID token");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to verify Google ID token: " + e.getMessage(), e);
        }
    }

}
