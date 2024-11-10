package com.example.course_be.controller;

import com.example.course_be.entity.Course;
import com.example.course_be.request.lesson.WatchHistoryRequest;
import com.example.course_be.response.lesson.LessonResponse;
import com.example.course_be.service.impl.LessonWatchHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/lesson-watch-history")
public class LessonWatchHistoryController {

    private final LessonWatchHistoryService lessonWatchHistoryService;


    public LessonWatchHistoryController(LessonWatchHistoryService lessonWatchHistoryService) {
        this.lessonWatchHistoryService = lessonWatchHistoryService;
    }

    // API để lưu lại thời gian xem của người dùng
    @PostMapping("/save")
    public ResponseEntity<String> saveWatchHistory(
            @RequestBody WatchHistoryRequest watchHistoryRequest) {

        // Lưu lại thời gian xem với userId
        lessonWatchHistoryService.saveWatchHistory(watchHistoryRequest);

        return ResponseEntity.ok("Watch history saved");
    }


    // API để lấy thời gian xem gần nhất của người dùng
    @GetMapping("/get")
    public ResponseEntity<Long> getWatchHistory(@RequestParam Long lessonId,
                                                @RequestParam Long  userId) {
        Long lastWatchedTime = lessonWatchHistoryService.getLastWatchedTime(userId, lessonId);
        return ResponseEntity.ok(lastWatchedTime);
    }
    @GetMapping("/watched")
    public ResponseEntity<List<Course>> getCourseWatchedByUserId(@RequestParam Long userId) {
       List<Course> courses = lessonWatchHistoryService.getCourseWatchedByUserId(userId);
        return ResponseEntity.ok(courses);
    }
    // 8 bai hoc xem nhieu nhat
    @GetMapping("/top8-watched")
    public ResponseEntity<List<LessonResponse>> getTop8MostWatchedLessons() {
        List<LessonResponse> lessons = lessonWatchHistoryService.getTop8MostWatchedLessons();
        return ResponseEntity.ok(lessons);
    }
}
