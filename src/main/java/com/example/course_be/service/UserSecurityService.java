package com.example.course_be.service;

import com.example.course_be.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserSecurityService extends UserDetailsService {
    public User findByUserName(String userName);
}
