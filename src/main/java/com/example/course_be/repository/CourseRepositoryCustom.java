package com.example.course_be.repository;

import com.example.course_be.entity.Course;

import java.util.List;

public interface CourseRepositoryCustom {
    List<Course> searchCoursesByKeywords(String keywords);
}
