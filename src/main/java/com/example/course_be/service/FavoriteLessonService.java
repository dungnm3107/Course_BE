package com.example.course_be.service;


import com.example.course_be.entity.FavoriteLesson;
import com.example.course_be.entity.Lesson;
import com.example.course_be.request.lesson.FavoriteLessonRequest;
import com.example.course_be.response.lesson.FavoriteLessonResponse;

import java.util.List;

public interface FavoriteLessonService {
    FavoriteLessonResponse createOrUpdateFavoriteLesson(FavoriteLessonRequest request);
    List<FavoriteLessonResponse> getFavoriteLessonsByUserId(Long userId);
    List<Lesson> getTop4MostFavoritedLessons();
}

