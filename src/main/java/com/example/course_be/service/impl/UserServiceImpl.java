package com.example.course_be.service.impl;

import com.example.course_be.entity.*;
import com.example.course_be.exceptions.AppException;
import com.example.course_be.exceptions.ErrorCode;
import com.example.course_be.repository.*;
import com.example.course_be.request.user.LoginRequest;
import com.example.course_be.request.user.UserRequest;
import com.example.course_be.request.user.UserUpdateRequest;
import com.example.course_be.response.JwtResponse;
import com.example.course_be.response.course.CourseStatisticResponse;
import com.example.course_be.response.user.UserRegistrationStatisticsResponse;
import com.example.course_be.response.user.UserResponse;
import com.example.course_be.service.JwtService;
import com.example.course_be.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CourseRepository courseRepository;
    private final UserCourseRepository userCourseRepository;


    @Autowired
    public UserServiceImpl(BCryptPasswordEncoder passwordEncoder, UserRepository userRepository, RoleRepository roleRepository, AuthenticationManager authenticationManager, JwtService jwtService, CourseRepository courseRepository, UserCourseRepository userCourseRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.courseRepository = courseRepository;
        this.userCourseRepository = userCourseRepository;
    }


    @Override
    @Transactional
    public boolean saveUser(UserRequest userRequest) {
        if (userRepository.existsByUserName(userRequest.getUserName())) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
        }

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        try {
            User user = new User();
            String encodePassword = passwordEncoder.encode(userRequest.getPassword());
            user.setPassword(encodePassword);
            user.setAvatar("");
            user.setFacebookId("");
            user.setGoogleId("");
            List<Role> roleList = new ArrayList<>();
            roleList.add(roleRepository.findByRoleName("CUSTOMER"));
            user.setListRoles(roleList);
            user.setPhone(userRequest.getPhone());
            user.setGender(userRequest.getGender());
            user.setEmail(userRequest.getEmail());
            user.setFullName(userRequest.getFullName());
            user.setEmail(userRequest.getEmail());
            user.setUserName(userRequest.getUserName());
            user.setIsDeleted(false);
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED); // Ném lại nếu lỗi không xác định
        }
    }

    @Override
    public JwtResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword()));

            if (authentication.isAuthenticated()) {
                final String jwtToken = jwtService.generateToken(loginRequest.getUserName());
                return new JwtResponse(jwtToken);
            }

        } catch (Exception e) {
            throw new AppException(ErrorCode.PASS_USERNAME_INCORRECT);
        }
        return null;
    }

    @Override
    public String addCourseToUser(Long userId, Long courseId) {
        if (userId == null || courseId == null) {
            return "User or course not found";
        }
        if (userCourseRepository.existsByUserIdAndCourseId(userId, courseId)) {
            return "User is already enrolled in this course";
        }
        Optional<Course> course = Optional.ofNullable(courseRepository.findById(courseId).orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND)));
        Optional<User> user = Optional.ofNullable(userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));
        if (course.isPresent() && user.isPresent()) {
            if (course.get().getCoursePrice().compareTo(BigDecimal.ZERO) > 0) {
                UserCourse userCourse = new UserCourse();
                userCourse.setCourse(course.get());
                userCourse.setUser(user.get());
                userCourse.setStatus("PENDING");
                userCourseRepository.save(userCourse);
                return "Add course successfully, please wait for admin to process";
            } else {
                UserCourse userCourse = new UserCourse();
                userCourse.setCourse(course.get());
                userCourse.setUser(user.get());
                userCourse.setStatus("ENROLLED");
                userCourseRepository.save(userCourse);
                return "Course added successfully";
            }
        }
        return "Course not found to add";
    }

    //api get user by id
    @Override
    public Optional<User> getUserById(Long userId) {
        return userRepository.getUserById(userId);
    }


    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.getAllActiveUsers();
    }

    @Override
    public String updateUser(UserUpdateRequest userUpdateRequest) {
        Long userId = userUpdateRequest.getIdUser();
        Optional<User> user = Optional.ofNullable(userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));
        if (user.isPresent()) {
            user.get().setFullName(userUpdateRequest.getFullName());
            user.get().setUserName(userUpdateRequest.getUserName());
            user.get().setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));
            user.get().setEmail(userUpdateRequest.getEmail());
            user.get().setPhone(userUpdateRequest.getPhone());
            user.get().setGender(userUpdateRequest.getGender());
            user.get().setAvatar(userUpdateRequest.getAvatar());
            userRepository.save(user.get());
            return "User updated successfully";
        }
        return "User not found to update";
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUserName(username);
    }

    @Override
    public long getUserCount() {
        return userRepository.countByIsDeletedFalse();
    }


    // api delete user
    @Override
    public String deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setIsDeleted(true);
        userRepository.save(user);
        return "User deleted successfully";
    }

    // lay ngay tao user
    public Date getUserCreatedDate(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(User::getCreatedDate).orElse(null);
    }

    @Override
    public List<UserRegistrationStatisticsResponse> getUserRegistrationsByMonthAndYear(int year) {
        return userRepository.countUserRegistrationsByMonthAndYear(year);
    }

    @Override
    public List<UserResponse> searchUsers(String username) {
        if (username == null || username.isEmpty()) {
            return userRepository.getAllActiveUsers(); //get all
        }
        return userRepository.findUserByUserName(username);
    }
}
