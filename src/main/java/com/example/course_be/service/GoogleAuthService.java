package com.example.course_be.service;

import java.util.Map;

public interface GoogleAuthService {
    public Map<String, Object> googleLogin(String idTokenString);
}
