package com.example.course_be.controller;

import com.example.course_be.service.GoogleAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/SSO")
public class GoogleController {
    @Autowired
    private GoogleAuthService googleAuthService;

    @PostMapping("/loginGoogle")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> payload) {
        String idToken = payload.get("credential");
        if (idToken == null || idToken.isEmpty()) {
            return ResponseEntity.badRequest().body("Missing or invalid token");
        }
        try {
            Map<String, Object> loginResponse = googleAuthService.googleLogin(idToken);
            return ResponseEntity.ok(loginResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid Google token");
        }
    }


}
