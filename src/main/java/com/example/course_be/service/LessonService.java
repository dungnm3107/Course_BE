package com.example.course_be.service;

import com.example.course_be.request.lesson.LessonRequest;
import com.example.course_be.request.lesson.LessonUpdateRequest;
import com.example.course_be.response.chapter.ChapterResponse;
import com.example.course_be.response.lesson.LessonResponse;

import java.util.List;

public interface LessonService {

    String saveLesson(LessonRequest lessonRequest);

    String updateLesson(LessonUpdateRequest lessonUpdateRequest);

    LessonResponse getLessonByID(Long id);

    List<LessonResponse> getLessonByChapterId(Long id);

    List<LessonResponse> getAllLesson();
    String deleteLessonById(Long lessonId);
}
