package com.example.course_be.controller;

import com.example.course_be.entity.Course;
import com.example.course_be.exceptions.AppException;
import com.example.course_be.request.course.CourseRequest;
import com.example.course_be.request.course.CourseUpdateRequest;
import com.example.course_be.response.course.CourseStatisticResponse;
import com.example.course_be.response.error.ApiResponse;
import com.example.course_be.response.course.CourseResponse;
import com.example.course_be.service.CourseService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/course")
@Slf4j
public class CourseController {

    private CourseService courseService;

    @Autowired
    public void setCourseService(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveCourse(@Valid @RequestBody CourseRequest courseRequest) {
        try {
            String isSaved = courseService.saveCourse(courseRequest);

            return getResponseEntity(isSaved);
        } catch (AppException e) {
            return ResponseEntity.badRequest().body(buildErrorResponse(e));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PostMapping("/upload-cover")
    public ResponseEntity<String> uploadCover(@RequestParam("file") MultipartFile file) {
        try {
            String filePath = courseService.uploadFile(file);
            return ResponseEntity.ok(filePath);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed: " + e.getMessage());
        }
    }


    @GetMapping("/search")
    public ResponseEntity<List<Course>> searchCourses(@RequestParam String keyword) {
        List<Course> courses = courseService.searchCourses(keyword);
        return ResponseEntity.ok(courses);
    }


    @GetMapping("/get/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Long id) {
        try {
            if (id != null) {
                ApiResponse<CourseResponse> apiResponse = new ApiResponse<>();
                CourseResponse course = courseService.getCourseById(id);
                apiResponse.setCode(200);
                apiResponse.setResult(course);
                return ResponseEntity.ok(apiResponse);
            }
            return ResponseEntity.badRequest().body("Course not found");
        } catch (AppException e) {
            ApiResponse<String> errorResponse = new ApiResponse<>();
            errorResponse.setCode(e.getErrorCode().getCode());
            errorResponse.setMessage(e.getErrorCode().getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        // Lấy danh sách khóa học từ service
        List<CourseResponse> listCourseResponses = courseService.getAllCourses();

        // Trả về danh sách khóa học với mã thành công 200
        return ResponseEntity.ok(listCourseResponses);
    }


    @GetMapping("/free")
    public ResponseEntity<?> getAllFreeCourses() {
        try {
            List<CourseResponse> listCourseResponses = courseService.getAllFreeCourses();
            ApiResponse<List<CourseResponse>> apiResponse = new ApiResponse<>();
            apiResponse.setCode(200);
            apiResponse.setResult(listCourseResponses);
            return ResponseEntity.ok(apiResponse);
        } catch (AppException e) {
            ApiResponse<String> errorResponse = new ApiResponse<>();
            errorResponse.setCode(e.getErrorCode().getCode());
            errorResponse.setMessage(e.getErrorCode().getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/paid")
    public ResponseEntity<?> getAllPaidCourses() {
        try {
            List<CourseResponse> listCourseResponses = courseService.getAllPaidCourses();
            ApiResponse<List<CourseResponse>> apiResponse = new ApiResponse<>();
            apiResponse.setCode(200);
            apiResponse.setResult(listCourseResponses);
            return ResponseEntity.ok(apiResponse);
        } catch (AppException e) {
            ApiResponse<String> errorResponse = new ApiResponse<>();
            errorResponse.setCode(e.getErrorCode().getCode());
            errorResponse.setMessage(e.getErrorCode().getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/statistics")
    public CourseStatisticResponse getCourseStatistics() {
        return courseService.getCourseStatistic();
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<String> deleteCourseById(@PathVariable Long courseId) {
            String isDeleted = courseService.deleteCourseById(courseId);
            return ResponseEntity.ok(isDeleted);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateCourse(@Valid @RequestBody CourseUpdateRequest courseUpdateRequest) {
        try {
            String isUpdated = courseService.updateCourse(courseUpdateRequest);
            return getResponseEntity(isUpdated);
        } catch (AppException e) {
            return ResponseEntity.badRequest().body(buildErrorResponse(e));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/changeStatus/{courseId}")
    public ResponseEntity<?> changeStatusCourse(@PathVariable Long courseId) {
        try {
            String isUpdated = courseService.changeStatusCourse(courseId);
            return getResponseEntity(isUpdated);
        } catch (AppException e) {
            return ResponseEntity.badRequest().body(buildErrorResponse(e));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private ResponseEntity<?> getResponseEntity(String isUpdated) {
        if (isUpdated != null) {
            ApiResponse<String> apiResponse = new ApiResponse<>();
            apiResponse.setCode(200);
            apiResponse.setMessage(isUpdated);
            return ResponseEntity.ok(apiResponse);
        }
        return ResponseEntity.badRequest().body(isUpdated);
    }

    private ApiResponse<String> buildErrorResponse(AppException e) {
        ApiResponse<String> errorResponse = new ApiResponse<>();
        errorResponse.setCode(e.getErrorCode().getCode());
        errorResponse.setMessage(e.getMessage());
        return errorResponse;
    }


}
