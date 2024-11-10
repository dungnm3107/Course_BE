package com.example.course_be.controller;





import com.example.course_be.entity.FavoriteLesson;
import com.example.course_be.entity.Lesson;
import com.example.course_be.request.lesson.FavoriteLessonRequest;
import com.example.course_be.response.lesson.FavoriteLessonResponse;
import com.example.course_be.service.FavoriteLessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/favorite-lessons")
public class FavoriteLessonController {

    @Autowired
    private FavoriteLessonService favoriteLessonService;

    @PostMapping("/toggle")
    public ResponseEntity<?> toggleFavoriteLesson(@RequestBody FavoriteLessonRequest request) {
        try {
            FavoriteLessonResponse favoriteLessonResponse = favoriteLessonService.createOrUpdateFavoriteLesson(request);
            return ResponseEntity.ok(favoriteLessonResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<FavoriteLessonResponse>> getFavoriteLessons(@RequestParam Long userId) {
        try {
            List<FavoriteLessonResponse> favoriteLessons = favoriteLessonService.getFavoriteLessonsByUserId(userId);
            return ResponseEntity.ok(favoriteLessons);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/top4")
    public ResponseEntity<List<Lesson>> getTop4MostFavoritedLessons() {
        try {
            List<Lesson> lessons = favoriteLessonService.getTop4MostFavoritedLessons();
            return ResponseEntity.ok(lessons);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}


