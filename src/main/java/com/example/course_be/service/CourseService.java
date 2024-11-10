package com.example.course_be.service;

import com.example.course_be.entity.Course;
import com.example.course_be.request.course.CourseRequest;
import com.example.course_be.request.course.CourseUpdateRequest;
import com.example.course_be.response.course.CourseResponse;
import com.example.course_be.response.course.CourseStatisticResponse;
import jakarta.transaction.Transactional;

import java.util.List;

public interface CourseService {

    String saveCourse(CourseRequest courseRequest);

    CourseResponse getCourseById(Long courseId);

    List<CourseResponse> getAllCourses();

    String deleteCourseById(Long courseId);



    String updateCourse(CourseUpdateRequest courseUpdateRequest);

    String changeStatusCourse(Long courseId);

    List<CourseResponse> getAllPaidCourses();

    List<CourseResponse> getAllFreeCourses();

    List<Course> searchCourses(String keyword);
    CourseStatisticResponse getCourseStatistic();

}
