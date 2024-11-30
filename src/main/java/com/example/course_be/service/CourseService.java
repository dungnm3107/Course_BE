package com.example.course_be.service;

import com.example.course_be.entity.Course;
import com.example.course_be.request.course.CourseRequest;
import com.example.course_be.request.course.CourseUpdateRequest;
import com.example.course_be.response.course.CourseResponse;
import com.example.course_be.response.course.CourseStatisticResponse;
import jakarta.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CourseService {

    String saveCourse(CourseRequest courseRequest);

    CourseResponse getCourseById(Long courseId);

    List<CourseResponse> getAllCourses();

    String deleteCourseById(Long courseId);

    String uploadFile(MultipartFile file) throws IOException;

    String updateCourse(CourseUpdateRequest courseUpdateRequest);

    String changeStatusCourse(Long courseId);

    List<CourseResponse> getAllPaidCourses();

    List<CourseResponse> getAllFreeCourses();

    List<Course> searchCourses(String keyword);
    CourseStatisticResponse getCourseStatistic();

}
