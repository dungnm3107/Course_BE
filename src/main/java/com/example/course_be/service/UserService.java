package com.example.course_be.service;

import com.example.course_be.entity.User;
import com.example.course_be.request.user.LoginRequest;
import com.example.course_be.request.user.UserRequest;
import com.example.course_be.request.user.UserUpdateRequest;
import com.example.course_be.response.JwtResponse;
import com.example.course_be.response.course.CourseStatisticResponse;
import com.example.course_be.response.user.UserRegistrationStatisticsResponse;
import com.example.course_be.response.user.UserResponse;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface UserService {
    boolean saveUser(UserRequest userRequest);

    JwtResponse login(LoginRequest loginRequest);

    String addCourseToUser(Long userId, Long courseId);

    Optional<User> getUserById(Long userId);

    List<UserResponse> getAllUsers();

    String updateUser(UserUpdateRequest userUpdateRequest);

    User getUserByUsername(String username);

    long getUserCount();


    String deleteUser(Long userId);

    List<UserRegistrationStatisticsResponse> getUserRegistrationsByMonthAndYear(int year);
}
