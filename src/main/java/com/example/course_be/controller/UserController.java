package com.example.course_be.controller;

import com.example.course_be.entity.User;
import com.example.course_be.exceptions.AppException;
import com.example.course_be.request.user.LoginRequest;
import com.example.course_be.request.user.UserCourseRequest;
import com.example.course_be.request.user.UserRequest;
import com.example.course_be.request.user.UserUpdateRequest;
import com.example.course_be.response.error.ApiResponse;
import com.example.course_be.response.JwtResponse;
import com.example.course_be.response.user.UserRegistrationStatisticsResponse;
import com.example.course_be.response.user.UserResponse;
import com.example.course_be.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {


    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody  @Valid UserRequest userRequest) {
        try {
            if (userRequest != null) {
                boolean isSaved = userService.saveUser(userRequest);
                if (isSaved) {
                    ApiResponse<User> apiResponse = new ApiResponse<>();
                    apiResponse.setCode(201);
                    apiResponse.setMessage("User registered successfully");
                    return ResponseEntity.ok(apiResponse);
                }
            }
            return ResponseEntity.badRequest().body("User registration failed");
        } catch (AppException e) {
            ApiResponse<String> errorResponse = new ApiResponse<>();
            errorResponse.setCode(e.getErrorCode().getCode());
            errorResponse.setMessage(e.getErrorCode().getMessage());
            return ResponseEntity.status(e.getErrorCode().getStatusCode()).body(errorResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest) {
        try {
            if (loginRequest.getUserName() != null && loginRequest.getPassword() != null) {
                JwtResponse jwtResponse = userService.login(loginRequest);
                ApiResponse<JwtResponse> apiResponse = new ApiResponse<>();
                apiResponse.setCode(200);
                apiResponse.setMessage("Login successful");
                apiResponse.setResult(jwtResponse);
                return ResponseEntity.ok(apiResponse);
            }
        } catch (AppException e) {
            ApiResponse<String> errorResponse = new ApiResponse<>();
            errorResponse.setCode(e.getErrorCode().getCode());
            errorResponse.setMessage(e.getErrorCode().getMessage());
            return ResponseEntity.status(e.getErrorCode().getStatusCode()).body(errorResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.badRequest().body(new ApiResponse<>(400, "Login failed", null));
    }

    @PostMapping("/addUserCourse")
    public ResponseEntity<?> addUserCourse(@RequestBody @Valid UserCourseRequest userCourseRequest){
        try {
            if (userCourseRequest != null) {
                String message = userService.addCourseToUser(userCourseRequest.getUserId(), userCourseRequest.getCourseId());
                ApiResponse<String> apiResponse = new ApiResponse<>();
                apiResponse.setCode(200);
                apiResponse.setMessage(message);
                return ResponseEntity.ok(apiResponse);
            }
            return ResponseEntity.badRequest().body("Add course to user failed");
        } catch (AppException e) {
            ApiResponse<String> errorResponse = new ApiResponse<>();
            errorResponse.setCode(e.getErrorCode().getCode());
            errorResponse.setMessage(e.getErrorCode().getMessage());
            return ResponseEntity.status(e.getErrorCode().getStatusCode()).body(errorResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserInfo(@PathVariable Long userId){
        try {
            if (userId != null) {
                return ResponseEntity.ok(userService.getUserById(userId));
            }
            return ResponseEntity.badRequest().body("User not found");
        } catch (AppException e) {
            ApiResponse<String> errorResponse = new ApiResponse<>();
            errorResponse.setCode(e.getErrorCode().getCode());
            errorResponse.setMessage(e.getErrorCode().getMessage());
            return ResponseEntity.status(e.getErrorCode().getStatusCode()).body(errorResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping()
    public ResponseEntity<?> getAllUsers() {
        try {
            ApiResponse<List<UserResponse>> apiResponse = new ApiResponse<>();
            apiResponse.setCode(200);
            apiResponse.setResult(userService.getAllUsers());
            return ResponseEntity.ok(apiResponse);
        } catch (AppException e) {
            ApiResponse<String> errorResponse = new ApiResponse<>();
            errorResponse.setCode(e.getErrorCode().getCode());
            errorResponse.setMessage(e.getErrorCode().getMessage());
            return ResponseEntity.status(e.getErrorCode().getStatusCode()).body(errorResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/updateUser")
    public ResponseEntity<?> updateUser(@Valid @RequestBody  UserUpdateRequest userUpdateRequest){
        try {
            if (userUpdateRequest != null) {
                String message = userService.updateUser(userUpdateRequest);
                ApiResponse<String> apiResponse = new ApiResponse<>();
                apiResponse.setCode(200);
                apiResponse.setMessage(message);
                return ResponseEntity.ok(apiResponse);
            }
            return ResponseEntity.badRequest().body("Update user failed");
        } catch (AppException e) {
            ApiResponse<String> errorResponse = new ApiResponse<>();
            errorResponse.setCode(e.getErrorCode().getCode());
            errorResponse.setMessage(e.getErrorCode().getMessage());
            return ResponseEntity.status(e.getErrorCode().getStatusCode()).body(errorResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            String username = userDetails.getUsername(); // Trích xuất username tu JWT token
            User userResponse = userService.getUserByUsername(username);
            return ResponseEntity.ok(userResponse);
        } catch (AppException e) {
            ApiResponse<String> errorResponse = new ApiResponse<>();
            errorResponse.setCode(e.getErrorCode().getCode());
            errorResponse.setMessage(e.getErrorCode().getMessage());
            return ResponseEntity.status(e.getErrorCode().getStatusCode()).body(errorResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // get so luong user
    @GetMapping("/count")
    public ResponseEntity<Long> getCountUser() {
        long userCount = userService.getUserCount();
        return ResponseEntity.ok(userCount);
    }
    // thong ke user đăng kí
    @GetMapping("/registration-statistics")
    public ResponseEntity<List<UserRegistrationStatisticsResponse>> getUserRegistrationStatistics(
            @RequestParam("year") int year) {
        List<UserRegistrationStatisticsResponse> statistics = userService.getUserRegistrationsByMonthAndYear(year);
        return ResponseEntity.ok(statistics);
    }
    // api delete user
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long userId) {
        String isDeleted = userService.deleteUser(userId);
        return ResponseEntity.ok(isDeleted);
    }

}
