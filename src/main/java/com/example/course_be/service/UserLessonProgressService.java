package com.example.course_be.service;

import com.example.course_be.entity.UserLessonProgress;
import com.example.course_be.request.lesson.UserLessonProgressRequest;
import com.example.course_be.response.lesson.UserLessonProgressResponse;

import java.util.List;

public interface UserLessonProgressService {
    UserLessonProgressResponse saveProgress(UserLessonProgressRequest request);
    List<UserLessonProgressResponse> getProgressByUserId(Long userId);
}